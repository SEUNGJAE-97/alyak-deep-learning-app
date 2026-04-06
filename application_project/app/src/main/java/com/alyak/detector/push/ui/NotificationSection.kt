package com.alyak.detector.push.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alyak.detector.R
import com.alyak.detector.feature.map.ui.components.FilterButton
import com.alyak.detector.push.dto.NotificationEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun NotificationEntity.toNotificationItemUi(): NotificationItem {
    val fmt = SimpleDateFormat("M/d HH:mm", Locale.getDefault())
    val normalizedType = when (val t = type.orEmpty()) {
        "FAMILY_INVITE" -> "FAMILY"
        else -> t.ifBlank { "GENERAL" }
    }
    return NotificationItem(
        id = notificationId,
        title = title,
        body = body,
        time = fmt.format(Date(timestamp)),
        type = normalizedType,
        isRead = isRead,
        inviterUserId = inviterUserId,
    )
}

data class NotificationItem(
    val id: Int,
    val title: String,
    val body: String,
    val time: String,
    val type: String,
    val isRead: Boolean = false,
    val inviterUserId: Long? = null,
)

private enum class NotificationFilterTab(val label: String) {
    ALL("전체"),
    UNREAD("안 읽음"),
    READ("읽음"),
}

@Composable
fun NotificationSection(
    notifications: List<NotificationItem>,
    showHeader: Boolean = true,
    onFamilyInviteAccept: ((NotificationItem) -> Unit)? = null,
    onFamilyInviteReject: ((NotificationItem) -> Unit)? = null,
) {
    var selectedFilter by remember { mutableStateOf(NotificationFilterTab.ALL) }
    val filtered = remember(notifications, selectedFilter) {
        when (selectedFilter) {
            NotificationFilterTab.ALL -> notifications
            NotificationFilterTab.UNREAD -> notifications.filter { !it.isRead }
            NotificationFilterTab.READ -> notifications.filter { it.isRead }
        }
    }

    LaunchedEffect(notifications.isEmpty()) {
        if (notifications.isEmpty()) {
            selectedFilter = NotificationFilterTab.ALL
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA)),
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
                    onSelect = { selectedFilter = it },
                )
            }
        }

        if (filtered.isEmpty()) {
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
                    Text(message, color = Color.Gray, fontSize = 15.sp)
                }
            }
        } else {
            items(filtered, key = { it.id }) { notification ->
                NotificationCard(
                    item = notification,
                    onFamilyInviteAccept = onFamilyInviteAccept,
                    onFamilyInviteReject = onFamilyInviteReject,
                )
            }
        }
    }
}

@Composable
private fun NotificationFilterChipRow(
    selected: NotificationFilterTab,
    onSelect: (NotificationFilterTab) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
}

@Composable
fun NotificationCard(
    item: NotificationItem,
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
    val notificationIconTint = when {
        !item.isRead && item.type == "FAMILY" -> Color(0xFF1976D2)
        !item.isRead -> primaryBlue
        item.type == "FAMILY" -> Color(0xFF90CAF9)
        else -> Color(0xFFB0BEC5)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {

        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (hasFamilyInviteActions) {
                            Modifier.clickable { inviteExpanded = !inviteExpanded }
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
                        .background(
                            if (item.type == "FAMILY") Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = notificationIconTint,
                        modifier = Modifier.size(24.dp)
                    )
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
                            color = Color(0xFF212529)
                        )
                        Text(
                            text = item.time,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.body,
                        fontSize = 14.sp,
                        color = Color(0xFF495057),
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
                    tint = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 10.dp, bottom = 10.dp)
                        .clickable { inviteExpanded = !inviteExpanded }
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
                    HorizontalDivider(color = Color(0xFFEEEEEE))
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
                            color = Color(0xFFE0E0E0),
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
                                color = Color.White,
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
            type = "FAMILY",
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