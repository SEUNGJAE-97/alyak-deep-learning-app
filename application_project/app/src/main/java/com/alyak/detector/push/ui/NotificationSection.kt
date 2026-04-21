package com.alyak.detector.push.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.feature.map.ui.components.FilterButton
import com.alyak.detector.push.ui.model.NotificationItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ClearReadSlideOutMs = 400

private enum class NotificationFilterTab(val label: String) {
    ALL("전체"),
    UNREAD("안 읽음"),
    READ("읽음"),
}

@Composable
fun NotificationSection(
    notifications: List<NotificationItem>,
    showHeader: Boolean = true,
    onNotificationClick: ((NotificationItem) -> Unit)? = null,
    onMarkAllAsRead: (() -> Unit)? = null,
    onClearReadNotifications: (() -> Unit)? = null,
    onFamilyInviteAccept: ((NotificationItem) -> Unit)? = null,
    onFamilyInviteReject: ((NotificationItem) -> Unit)? = null,
) {
    var selectedFilter by remember { mutableStateOf(NotificationFilterTab.ALL) }
    var filterSnapshotIds by remember { mutableStateOf<List<Int>>(emptyList()) }

    val onFilterTabSelected: (NotificationFilterTab) -> Unit = { tab ->
        selectedFilter = tab
        filterSnapshotIds = when (tab) {
            NotificationFilterTab.ALL -> emptyList()
            NotificationFilterTab.UNREAD -> notifications.filter { !it.isRead }.map { it.id }
            NotificationFilterTab.READ -> notifications.filter { it.isRead }.map { it.id }
        }
    }

    val displayedNotifications = remember(notifications, selectedFilter, filterSnapshotIds) {
        when (selectedFilter) {
            NotificationFilterTab.ALL -> notifications
            NotificationFilterTab.UNREAD,
            NotificationFilterTab.READ,
                -> filterSnapshotIds.mapNotNull { id -> notifications.find { it.id == id } }
        }
    }

    LaunchedEffect(notifications.isEmpty()) {
        if (notifications.isEmpty()) {
            selectedFilter = NotificationFilterTab.ALL
            filterSnapshotIds = emptyList()
        }
    }

    var exitingNotificationIds by remember { mutableStateOf<Set<Int>>(emptySet()) }
    var isClearingRead by remember { mutableStateOf(false) }
    val clearReadScope = rememberCoroutineScope()
    val runExitAnimation: (List<Int>, () -> Unit) -> Unit = { ids, action ->
        if (!isClearingRead && ids.isNotEmpty()) {
            clearReadScope.launch {
                isClearingRead = true
                ids.forEachIndexed { index, id ->
                    launch {
                        delay(index * 50L)
                        exitingNotificationIds = exitingNotificationIds + id
                    }
                }
                delay(ClearReadSlideOutMs.toLong() + (ids.size * 50L))

                action()
                exitingNotificationIds = emptySet()
                isClearingRead = false
            }
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.notification_screen_background)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (showHeader) {
            item {
                Text(
                    text = "알림",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        if (notifications.isNotEmpty()) {
            item {
                NotificationFilterChipRow(
                    selected = selectedFilter,
                    onSelect = onFilterTabSelected,
                    onMarkAllAsRead = onMarkAllAsRead?.let { action ->
                        {
                            val unreadIds =
                                displayedNotifications.filter { !it.isRead }.map { it.id }
                            runExitAnimation(unreadIds, action)
                        }
                    },
                    onClearReadNotifications = onClearReadNotifications?.let { action ->
                        {
                            val readIds = displayedNotifications.filter { it.isRead }.map { it.id }
                            runExitAnimation(readIds, action)
                        }
                    }
                )
            }
        }

        if (displayedNotifications.isEmpty()) {
            item {
                val message = if (notifications.isEmpty()) {
                    "알림이 없습니다."
                } else {
                    "해당 조건의 알림이 없습니다."
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        message,
                        color = colorResource(R.color.notification_text_muted),
                        fontSize = 15.sp,
                    )
                }
            }
        } else {
            items(displayedNotifications, key = { it.id }) { notification ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(
                            fadeInSpec = null,
                            fadeOutSpec = tween(ClearReadSlideOutMs),
                            placementSpec = spring(stiffness = Spring.StiffnessLow)
                        )
                ) {
                    AnimatedVisibility(
                        visible = notification.id !in exitingNotificationIds,
                        exit = slideOutHorizontally(
                            animationSpec = tween(ClearReadSlideOutMs),
                            targetOffsetX = { fullWidth -> fullWidth },
                        ) + fadeOut(tween(ClearReadSlideOutMs)),
                        enter = fadeIn(tween(220)),
                    ) {
                        NotificationCard(
                            item = notification,
                            onNotificationClick = onNotificationClick,
                            onFamilyInviteAccept = onFamilyInviteAccept,
                            onFamilyInviteReject = onFamilyInviteReject,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationFilterChipRow(
    selected: NotificationFilterTab,
    onSelect: (NotificationFilterTab) -> Unit,
    onMarkAllAsRead: (() -> Unit)?,
    onClearReadNotifications: (() -> Unit)?,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            NotificationFilterTab.entries.forEach { tab ->
                FilterButton(
                    text = tab.label,
                    isSelected = selected == tab,
                    onClick = { onSelect(tab) },
                )
            }
        }

        val actionLabel: String?
        val actionOnClick: (() -> Unit)?
        when (selected) {
            NotificationFilterTab.ALL,
            NotificationFilterTab.UNREAD,
                -> {
                actionLabel = if (onMarkAllAsRead != null) "모두 읽음" else null
                actionOnClick = onMarkAllAsRead
            }

            NotificationFilterTab.READ -> {
                actionLabel = if (onClearReadNotifications != null) "비우기" else null
                actionOnClick = onClearReadNotifications
            }
        }
        if (actionLabel != null && actionOnClick != null) {
            Text(
                text = actionLabel,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable(onClick = actionOnClick),
                color = colorResource(R.color.notification_text_muted),
                fontSize = 12.sp,
                fontWeight = FontWeight.Thin,
                maxLines = 2,
            )
        }
    }
}

@Composable
fun NotificationCard(
    item: NotificationItem,
    onNotificationClick: ((NotificationItem) -> Unit)? = null,
    onFamilyInviteAccept: ((NotificationItem) -> Unit)? = null,
    onFamilyInviteReject: ((NotificationItem) -> Unit)? = null,
) {
    val primaryBlue = colorResource(R.color.primaryBlue)
    val hasFamilyInviteActions =
        item.inviterUserId != null &&
                onFamilyInviteAccept != null &&
                onFamilyInviteReject != null
    var inviteExpanded by remember(item.id) { mutableStateOf(false) }
    val expandFadeSpec = tween<Float>(280, easing = FastOutSlowInEasing)
    val expandSizeSpec = tween<IntSize>(280, easing = FastOutSlowInEasing)
    val category = NotificationCategory.from(item)
    val isFamilyBlueTheme =
        category == NotificationCategory.FAMILY_INVITE ||
                category == NotificationCategory.FAMILY_ACTIVITY
    val readIconBackground = colorResource(R.color.notification_icon_read_background)
    val readIconTint = colorResource(R.color.notification_icon_read_tint)
    val familyIconTint = colorResource(R.color.notification_family_icon_tint)
    val familyIconCircle = colorResource(R.color.notification_family_icon_circle)
    val iconCircleNeutral = colorResource(R.color.notification_icon_circle_neutral)
    val notificationIconTint =
        if (item.isRead) readIconTint
        else when {
            isFamilyBlueTheme -> familyIconTint
            else -> primaryBlue
        }
    val notificationIconCircleColor =
        if (item.isRead) readIconBackground
        else if (isFamilyBlueTheme) familyIconCircle
        else iconCircleNeutral

    fun onMainAreaInteract() {
        if (!item.isRead) onNotificationClick?.invoke(item)
        if (hasFamilyInviteActions) inviteExpanded = !inviteExpanded
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorResource(R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (hasFamilyInviteActions || onNotificationClick != null) {
                            Modifier.clickable { onMainAreaInteract() }
                        } else {
                            Modifier
                        },
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. 아이콘 영역
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(notificationIconCircleColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (category.useMaterialBell) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = notificationIconTint,
                            modifier = Modifier.size(24.dp),
                        )
                    } else {
                        Icon(
                            painter = painterResource(checkNotNull(category.iconRes)),
                            contentDescription = null,
                            tint = notificationIconTint,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 2. 텍스트 영역
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = colorResource(R.color.notification_title_text)
                        )
                        Text(
                            text = item.time,
                            fontSize = 12.sp,
                            color = colorResource(R.color.notification_text_muted)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.body,
                        fontSize = 14.sp,
                        color = colorResource(R.color.notification_body_text),
                        lineHeight = 20.sp
                    )
                }
            }

            if (hasFamilyInviteActions) {
                Icon(
                    imageVector = if (inviteExpanded) {
                        Icons.Default.KeyboardArrowUp
                    } else {
                        Icons.Default.KeyboardArrowDown
                    },
                    contentDescription = if (inviteExpanded) "접기" else "펼쳐서 수락·거절",
                    tint = colorResource(R.color.notification_text_muted),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 10.dp, bottom = 10.dp)
                        .clickable { onMainAreaInteract() }
                        .size(24.dp),
                )
            }
        }

        AnimatedVisibility(
            visible = hasFamilyInviteActions && inviteExpanded,
            enter = fadeIn(expandFadeSpec) + expandVertically(
                animationSpec = expandSizeSpec,
                expandFrom = Alignment.Top,
            ),
            exit = fadeOut(expandFadeSpec) + shrinkVertically(
                animationSpec = expandSizeSpec,
                shrinkTowards = Alignment.Top,
            ),
        ) {
            if (onFamilyInviteAccept != null && onFamilyInviteReject != null) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = colorResource(R.color.notification_divider))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable { onFamilyInviteReject(item) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "거절",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = primaryBlue,
                            )
                        }
                        VerticalDivider(
                            modifier = Modifier.fillMaxHeight(),
                            thickness = 1.dp,
                            color = colorResource(R.color.notification_divider_vertical),
                        )
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .background(primaryBlue)
                                .clickable { onFamilyInviteAccept(item) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "수락",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = colorResource(R.color.white),
                            )
                        }
                    }
                }
            }

        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
fun NotificationSectionPreview() {
    val mockData = listOf(
        NotificationItem(
            id = 1,
            title = "가족 초대",
            body = "김민수님이 가족 그룹에 초대했습니다.",
            time = "방금 전",
            type = "FAMILY_INVITE",
            isRead = false,
            inviterUserId = 100L,
        ),
        NotificationItem(
            2, "약 복용 알림", "오후 1시 비타민 복용 시간입니다.",
            "15분 전", "PILL", true
        ),
        NotificationItem(
            3, "가족 활동", "이영희님이 오늘 약을 모두 복용했습니다.",
            "2시간 전", "FAMILY", true
        )
    )

    MaterialTheme {
        NotificationSection(
            notifications = mockData,
            onFamilyInviteAccept = {},
            onFamilyInviteReject = {},
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F9FA)
@Composable
fun NotificationSectionPreviewEmpty() {
    MaterialTheme {
        NotificationSection(notifications = emptyList())
    }
}