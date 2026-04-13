package com.alyak.detector.feature.notification

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AlyakFirebaseMessagingService에서 포그라운드 푸시를 발행하고,
 * InAppPushBannerHost에서 구독해 인앱 배너로 표시합니다.
 */
@Singleton
class InAppPushNotifier @Inject constructor() {

    private val _events = MutableSharedFlow<InAppPushEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<InAppPushEvent> = _events.asSharedFlow()

    fun emit(event: InAppPushEvent) {
        _events.tryEmit(event)
    }
}
