package com.alyak.detector.feature.family.ui.main

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alyak.detector.core.auth.SessionManager
import com.alyak.detector.core.auth.TokenManager
import com.alyak.detector.core.auth.UserSession
import com.alyak.detector.core.network.ApiResult
import com.alyak.detector.core.util.AlarmScheduler
import com.alyak.detector.feature.family.data.api.FamilyApi
import com.alyak.detector.feature.family.data.model.AcceptFamilyInviteRequest
import com.alyak.detector.feature.family.data.model.DailyMedicationStat
import com.alyak.detector.feature.family.data.model.FamilyMember
import com.alyak.detector.feature.family.data.model.MedicineSchedule
import com.alyak.detector.feature.family.data.repository.FamilyRepo
import com.alyak.detector.feature.notification.alarm.MedicationAlarmScheduler
import com.alyak.detector.feature.notification.data.local.ScheduleBackupLocalRepository
import com.alyak.detector.feature.notification.data.local.dao.ScheduleBackupDao
import com.alyak.detector.feature.notification.data.local.entity.ScheduleBackupEntity
import com.alyak.detector.feature.notification.data.model.MedicationLogRequest
import com.alyak.detector.feature.notification.data.repository.MedicationLogRepository
import com.alyak.detector.feature.notification.data.repository.ScheduleRepository
import com.alyak.detector.feature.notification.schedule.NextMedicationUi
import com.alyak.detector.feature.notification.schedule.OccurrencePriority
import com.alyak.detector.feature.notification.schedule.ScheduleOccurrenceHelper
import com.alyak.detector.push.dao.NotificationDao
import com.alyak.detector.push.ui.model.NotificationItem
import com.alyak.detector.push.ui.model.toNotificationItemUi
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@HiltViewModel
class MainViewModel @Inject constructor(
    private val familyRepo: FamilyRepo,
    private val familyApi: FamilyApi,
    private val tokenManager: TokenManager,
    private val alarmScheduler: AlarmScheduler,
    private val sessionManager: SessionManager,
    private val notificationDao: NotificationDao,
    private val scheduleBackupDao: ScheduleBackupDao,
    private val scheduleRepository: ScheduleRepository,
    private val scheduleBackupLocalRepository: ScheduleBackupLocalRepository,
    private val medicationAlarmScheduler: MedicationAlarmScheduler,
    private val medicationLogRepository: MedicationLogRepository,
) : ViewModel() {

    private val _toastMessage = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toastMessage = _toastMessage.asSharedFlow()

    val unreadNotificationCount = notificationDao.getUnreadCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val notificationItems: StateFlow<List<NotificationItem>> =
        notificationDao.getAllNotificationsFlow()
            .map { list -> list.map { it.toNotificationItemUi() } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private var _selectedIndex by mutableIntStateOf(0)
    val selectedIndex: Int get() = _selectedIndex
    private var successRate by mutableIntStateOf(0)
    private var completeCount by mutableIntStateOf(0)
    private var missedCount by mutableIntStateOf(0)
    private var delayedCount by mutableIntStateOf(0)
    private var scheduledCount by mutableIntStateOf(0)
    private var totalCount = completeCount + missedCount + delayedCount + scheduledCount
    val _totalCount: Int get() = totalCount
    val dateFormatter = SimpleDateFormat("M/d", Locale.getDefault())
    val familySchedule: List<MedicineSchedule> get() = _familySchedules
    private val _familySchedules: SnapshotStateList<MedicineSchedule> = mutableStateListOf()

    private val _nextMedicationUi = MutableStateFlow<NextMedicationUi?>(null)
    val nextMedicationUi: StateFlow<NextMedicationUi?> = _nextMedicationUi.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    private val _familyMembers = mutableStateListOf<FamilyMember>()
    val familyMembers: List<FamilyMember> get() = _familyMembers
    val userName: StateFlow<String> = sessionManager.userSession
        .map { session ->
            when (session) {
                is UserSession.Authenticated -> session.userInfo.name
                else -> "로딩 중.."
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "로딩 중.."
        )

    /**
     * 선택된 멤버의 주간 통계 데이터
     * UI에서 dailyStatToBarSegments 함수로 BarSegments로 변환
     */
    val selectedMemberStats: List<DailyMedicationStat>
        get() = if (_familyMembers.isNotEmpty()) _familyMembers[_selectedIndex].weeklyMedicationStats else emptyList()

    init {
        fetchFamilyMembers()
        fetchSchedules()
        viewModelScope.launch { restoreScheduleBackupsFromServer() }
        viewModelScope.launch {
            combine(
                scheduleBackupDao.observeAll(),
                secondTickerFlow(),
            ) { entities, _ -> entities }
                .collect { entities ->
                    reconcile(entities)
                }
        }
    }

    /**
     * 앱 재설치 시 Room이 비어 있으므로, 서버에 남아 있는 [POST /api/schedule/backup] 데이터를
     * [GET /api/schedule/restore]로 받아 로컬에 맞춥니다.
     *
     * 이미 로컬에 일정이 있으면 건너뜁니다(복약 처리로 지운 행만 남은 경우 서버와 불일치할 수 있어
     * 매 화면 진입마다 전체 덮어쓰기는 하지 않음).
     */
    private suspend fun restoreScheduleBackupsFromServer() {
        val existing = withContext(Dispatchers.IO) { scheduleBackupDao.getAll() }
        if (existing.isNotEmpty()) return

        when (val result = scheduleRepository.restoreSchedules()) {
            is ApiResult.Success -> {
                val body = result.data ?: emptyList()
                withContext(Dispatchers.IO) {
                    scheduleBackupLocalRepository.replaceAllFromServerRestore(body)
                    medicationAlarmScheduler.rescheduleAllFromLocal(
                        scheduleBackupLocalRepository.getAllForAlarms(),
                    )
                }
            }

            is ApiResult.Error -> {
                Log.d(
                    "MainViewModel",
                    "schedule restore skipped: ${result.code} ${result.message}",
                )
            }

            is ApiResult.Exception -> {
                Log.d(
                    "MainViewModel",
                    "schedule restore failed: ${result.throwable.message}",
                )
            }
        }
    }

    private fun secondTickerFlow() = flow {
        while (true) {
            emit(Unit)
            delay(1000L)
        }
    }

    private suspend fun reconcile(entities: List<ScheduleBackupEntity>) {
        var list = entities
        while (true) {
            val now = System.currentTimeMillis()
            when (val p = ScheduleOccurrenceHelper.resolve(list, now)) {
                null -> {
                    _nextMedicationUi.value = null
                    return
                }

                is OccurrencePriority.Missed -> {
                    postSkippedAndDelete(p.entity, p.scheduledMillis)
                    list = scheduleBackupDao.getAll()
                    continue
                }

                is OccurrencePriority.Show -> {
                    _nextMedicationUi.value = p.ui
                    return
                }
            }
        }
    }

    private suspend fun postSkippedAndDelete(
        entity: ScheduleBackupEntity,
        scheduledMillis: Long,
    ) {
        val req = MedicationLogRequest(
            pillName = entity.pillName,
            dosage = entity.dosage,
            scheduledTime = millisToIsoLocal(scheduledMillis),
            takenTime = null,
        )
        medicationLogRepository.postLog(req).fold(
            onSuccess = { },
            onFailure = {
                _toastMessage.tryEmit(
                    if (it is IllegalStateException) "미복용 기록 전송에 실패했습니다."
                    else "네트워크 오류로 기록을 남기지 못했습니다.",
                )
                return
            },
        )
        scheduleBackupDao.deleteByScheduleId(entity.scheduleId)
        medicationAlarmScheduler.rescheduleAllFromLocal(scheduleBackupDao.getAll())
    }

    fun onMedicationCheckClick() {
        val ui = _nextMedicationUi.value ?: return
        if (!ui.canTapCheck) return
        viewModelScope.launch {
            postTakenAndDelete(ui)
        }
    }

    private suspend fun postTakenAndDelete(ui: NextMedicationUi) {
        val now = System.currentTimeMillis()
        val req = MedicationLogRequest(
            pillName = ui.entity.pillName,
            dosage = ui.entity.dosage,
            scheduledTime = millisToIsoLocal(ui.scheduledMillis),
            takenTime = millisToIsoLocal(now),
        )
        medicationLogRepository.postLog(req).fold(
            onSuccess = { },
            onFailure = {
                _toastMessage.tryEmit(
                    if (it is IllegalStateException) "복용 기록 전송에 실패했습니다."
                    else "네트워크 오류로 기록을 남기지 못했습니다.",
                )
                return
            },
        )
        scheduleBackupDao.deleteByScheduleId(ui.entity.scheduleId)
        medicationAlarmScheduler.rescheduleAllFromLocal(scheduleBackupDao.getAll())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun millisToIsoLocal(ms: Long): String {
        val zone = ZoneId.systemDefault()
        return Instant.ofEpochMilli(ms).atZone(zone).toLocalDateTime()
            .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }

    fun onItemSelected(index: Int) {
        _selectedIndex = index
        loadUserChartData()
    }

    fun refreshFamilyMembers() {
        fetchFamilyMembers()
    }

    private fun fetchFamilyMembers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            val apiResult = familyRepo.fetchMembers()

            when (apiResult) {
                is ApiResult.Success -> {
                    _familyMembers.clear()
                    _familyMembers.addAll(apiResult.data)
                    if (_familyMembers.isNotEmpty()) {
                        loadUserChartData()
                    }
                    _isLoading.value = false
                }

                is ApiResult.Error -> {
                    _errorMessage.value = apiResult.message
                    _isLoading.value = false
                }

                is ApiResult.Exception -> {
                    _errorMessage.value = "네트워크 연결을 확인해주세요."
                    _isLoading.value = false
                }
            }
        }
    }

    private fun loadUserChartData() {
        if (_familyMembers.isNotEmpty()) {
            successRate = _familyMembers[_selectedIndex].stats.successRate
            completeCount = _familyMembers[_selectedIndex].stats.completeCount
            missedCount = _familyMembers[_selectedIndex].stats.missedCount
            delayedCount = _familyMembers[_selectedIndex].stats.delayedCount
            scheduledCount = _familyMembers[_selectedIndex].stats.scheduledCount
        }
    }

    private fun fetchSchedules() {
        viewModelScope.launch {
            val apiResult = familyRepo.fetchSchedule()
            when (apiResult) {
                is ApiResult.Success -> {
                    val fetchSchedule = apiResult.data
                    _familySchedules.clear()
                    _familySchedules.addAll(fetchSchedule)
                }

                is ApiResult.Error -> {
                    Log.e("MainViewModel", "API Error: ${apiResult.code} - ${apiResult.message}")
                }

                is ApiResult.Exception -> {
                    Log.e("MainViewModel", "Network Exception: ${apiResult.throwable.message}")
                }
            }
        }
    }

    fun setAlarmForMedicine(timeLeftString: String) {
        val minutes = timeLeftString.toIntOrNull() ?: return
        alarmScheduler.scheduleAlarm(minutes)
    }

    fun onFamilyInviteAccept(item: NotificationItem) {
        val inviterId = item.inviterUserId ?: return
        viewModelScope.launch {
            try {
                val response = familyApi.acceptFamilyInvite(
                    AcceptFamilyInviteRequest(inviterId),
                )
                if (response.isSuccessful) {
                    notificationDao.deleteById(item.id)
                    refreshFamilyMembers()
                    _toastMessage.tryEmit("가족 초대를 수락했습니다.")
                } else {
                    _toastMessage.tryEmit("수락에 실패했습니다. (${response.code()})")
                }
            } catch (_: Exception) {
                _toastMessage.tryEmit("네트워크 오류가 발생했습니다.")
            }
        }
    }

    fun onFamilyInviteReject(item: NotificationItem) {
        viewModelScope.launch {
            notificationDao.deleteById(item.id)
            _toastMessage.tryEmit("초대를 거절했습니다.")
        }
    }

    fun markNotificationAsRead(item: NotificationItem) {
        if (item.isRead) return
        viewModelScope.launch {
            notificationDao.markAsRead(item.id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            notificationDao.markAllAsRead()
            _toastMessage.tryEmit("모든 알림을 읽음 처리했습니다.")
        }
    }

    fun clearReadNotifications() {
        viewModelScope.launch {
            notificationDao.deleteAllRead()
            _toastMessage.tryEmit("읽은 알림을 모두 삭제했습니다.")
        }
    }
}
