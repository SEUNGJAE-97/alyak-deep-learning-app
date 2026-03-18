package com.alyak.detector.feature.family.ui.invitation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sms
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.alyak.detector.feature.family.ui.invitation.component.EmailInputSection
import com.alyak.detector.feature.family.ui.invitation.component.InvitationOptionItem

@Composable
fun InvitationBottomSheet(
    viewModel: FamilyInvitationViewModel = hiltViewModel(),
    onDismiss: () -> Unit
) {
    var expandedOption by remember { mutableStateOf<InvitationOption?>(null) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.9f)
            .padding(16.dp)
            .imePadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // QR 코드 초대
            InvitationOptionItem(
                title = "QR 코드로 추가하기",
                description = "초대하고자 하는 사람의 QR 코드를 스캔",
                icon = Icons.Default.QrCodeScanner,
                isExpanded = expandedOption == InvitationOption.QR_CODE,
                onClick = {
                    expandedOption = if (expandedOption == InvitationOption.QR_CODE) null else InvitationOption.QR_CODE
                    /* QR코드 로직 실행 */
                }
            )

            // 이메일 초대
            InvitationOptionItem(
                title = "이메일로 추가하기",
                description = "초대하고자 하는 사람의 이메일을 입력",
                icon = Icons.Default.Email,
                isExpanded = expandedOption == InvitationOption.EMAIL,
                onClick = {
                    expandedOption = if (expandedOption == InvitationOption.EMAIL) null else InvitationOption.EMAIL
                }
            )

            AnimatedVisibility(
                visible = expandedOption == InvitationOption.EMAIL,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                EmailInputSection(
                    onSendClick = { email ->
                        onDismiss() // 전송 후 바텀시트 닫기
                    }
                )
            }

            // SMS 초대 (비활성화)
            InvitationOptionItem(
                title = "휴대폰 번호로 추가하기",
                description = "미구현",
                icon = Icons.Default.Sms,
                iconColor = Color.LightGray,
                onClick = { /* 처리 없음 */ }
            )
        }
    }
}