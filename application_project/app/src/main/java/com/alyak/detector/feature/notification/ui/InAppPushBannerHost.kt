package com.alyak.detector.feature.notification.ui

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import com.alyak.detector.feature.notification.InAppPushEvent
import com.alyak.detector.feature.notification.InAppPushNotifier
import com.alyak.detector.push.PushActionReceiver
import kotlinx.coroutines.delay

private const val AUTO_DISMISS_MS = 12_000L

private val InAppPushProgressGreen = Color(0xFF22C55E)
private val InAppPushProgressTrack = Color(0xFFE5E7EB)

/**
 * 포그라운드 푸시를 상단 카드 배너로 표시합니다. [content] 위에 겹쳐 그립니다.
 */
@Composable
fun InAppPushBannerHost(
    notifier: InAppPushNotifier,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    var visibleEvent by remember { mutableStateOf<InAppPushEvent?>(null) }
    val dismissProgress = remember { Animatable(1f) }

    LaunchedEffect(visibleEvent?.notificationId) {
        visibleEvent?.notificationId ?: return@LaunchedEffect
        dismissProgress.snapTo(1f)
        dismissProgress.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = AUTO_DISMISS_MS.toInt(),
                easing = LinearEasing,
            ),
        )
    }

    LaunchedEffect(notifier) {
        notifier.events.collect { event ->
            visibleEvent = event
        }
    }

    LaunchedEffect(visibleEvent) {
        val ev = visibleEvent ?: return@LaunchedEffect
        delay(AUTO_DISMISS_MS)
        if (visibleEvent?.notificationId == ev.notificationId) {
            visibleEvent = null
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        val event = visibleEvent
        AnimatedVisibility(
            visible = event != null,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp),
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
        ) {
            val e = event ?: return@AnimatedVisibility
            Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 4.dp),
                            ) {
                                Text(
                                    text = e.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = e.body,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 4,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            IconButton(
                                onClick = { visibleEvent = null },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "닫기",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }

                        if (e.isFamilyInvite) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.End,
                            ) {
                                TextButton(
                                    onClick = {
                                        context.sendBroadcast(
                                            Intent(context, PushActionReceiver::class.java).apply {
                                                action =
                                                    PushActionReceiver.ACTION_FAMILY_INVITE_REJECT
                                                putExtra(
                                                    PushActionReceiver.EXTRA_NOTIFICATION_ID,
                                                    e.notificationId,
                                                )
                                                putExtra(
                                                    PushActionReceiver.EXTRA_INVITER_USER_ID,
                                                    e.inviterUserId,
                                                )
                                                putExtra(
                                                    PushActionReceiver.EXTRA_INVITER_NAME,
                                                    e.inviterName,
                                                )
                                            },
                                        )
                                        NotificationManagerCompat.from(context)
                                            .cancel(e.notificationId)
                                        visibleEvent = null
                                    },
                                ) {
                                    Text("거절", color = Color(0xFF5864D9))
                                }
                                TextButton(
                                    onClick = {
                                        context.sendBroadcast(
                                            Intent(context, PushActionReceiver::class.java).apply {
                                                action =
                                                    PushActionReceiver.ACTION_FAMILY_INVITE_ACCEPT
                                                putExtra(
                                                    PushActionReceiver.EXTRA_NOTIFICATION_ID,
                                                    e.notificationId,
                                                )
                                                putExtra(
                                                    PushActionReceiver.EXTRA_INVITER_USER_ID,
                                                    e.inviterUserId,
                                                )
                                                putExtra(
                                                    PushActionReceiver.EXTRA_INVITER_NAME,
                                                    e.inviterName,
                                                )
                                            },
                                        )
                                        NotificationManagerCompat.from(context)
                                            .cancel(e.notificationId)
                                        visibleEvent = null
                                    },
                                ) {
                                    Text("수락", color = Color(0xFF5864D9))
                                }
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = { dismissProgress.value },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(
                                RoundedCornerShape(
                                    bottomStart = 12.dp,
                                    bottomEnd = 12.dp,
                                ),
                            ),
                        color = InAppPushProgressGreen,
                        trackColor = InAppPushProgressTrack,
                    )
                    }
                }
        }
    }
}
