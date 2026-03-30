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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.alyak.detector.feature.family.ui.invitation.component.EmailInputSection
import com.alyak.detector.feature.family.ui.invitation.component.InvitationOptionItem
import com.alyak.detector.feature.family.ui.invitation.component.QRDisplaySection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvitationBottomSheet(
    navController: NavController,
    viewModel: FamilyInvitationViewModel = hiltViewModel(),
    sheetState: SheetState,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scannedToken by viewModel.scannedInviteToken.collectAsState()
    var expandedOption by remember { mutableStateOf<InvitationOption?>(null) }
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    /* 만일 이메일 입력을 시도할 경우 0.9f까지 바텀시트를 올려준다.*/
    LaunchedEffect(expandedOption) {
        if (expandedOption != null) {
            sheetState.expand()
        } else {
            sheetState.partialExpand()
        }
    }

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
                    onDismiss()
                    navController.navigate("CameraScreenQr")
                }
            )
            if (!scannedToken.isNullOrBlank()) {
                Text(
                    text = "인식된 코드: $scannedToken",
                    color = Color(0xFF5864D9),
                    modifier = Modifier.padding(start = 68.dp, top = 2.dp, bottom = 8.dp)
                )
            }

            InvitationOptionItem(
                title = "QR 생성하기",
                description = "생성된 QR코드를 상대방에게 보여주세요",
                icon = Icons.Default.QrCodeScanner,
                isExpanded = expandedOption == InvitationOption.QR_CODE,
                onClick = {
                    if(expandedOption != InvitationOption.QR_CODE){
                        expandedOption = InvitationOption.QR_CODE
                        viewModel.onOptionSelected(InvitationOption.QR_CODE)
                    }else{
                        expandedOption = null
                    }
                }
            )

            AnimatedVisibility(
                visible = expandedOption == InvitationOption.QR_CODE,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                QRDisplaySection(
                    state = state,
                    onClickRegenerate = { viewModel.onClickRegenerate() }
                )
            }

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
                        scope.launch {
                            expandedOption = null
                            sheetState.partialExpand()
                        }
                        onDismiss()
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