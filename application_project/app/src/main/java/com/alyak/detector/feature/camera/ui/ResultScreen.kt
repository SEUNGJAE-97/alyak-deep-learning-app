package com.alyak.detector.feature.camera.ui

import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.alyak.detector.R
import com.alyak.detector.feature.camera.data.model.PillDetection
import com.alyak.detector.feature.camera.data.model.PillDetector
import com.alyak.detector.feature.pill.data.model.Pill
import com.alyak.detector.feature.pill.data.model.toPill
import com.alyak.detector.feature.pill.ui.search.components.PillInfoBox
import com.alyak.detector.feature.pill.ui.search.components.TextPlaceholder
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import java.util.Collections.emptyList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavController,
    viewModel: CameraViewModel,
) {
    val context = LocalContext.current
    val pillDetector = remember { PillDetector(context) }

    val bitmap by viewModel.capturedBitmap.collectAsState()
    val isSending by viewModel.isSending.collectAsState()
    val sendResult by viewModel.sendResult.collectAsState()
    val sendError by viewModel.sendError.collectAsState()
    val detectedObjects = remember { mutableStateOf<List<PillDetection>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    var showResultSheet by remember { mutableStateOf(false) }
    val resultSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetScope = rememberCoroutineScope()
    val imageLoader = remember {
        ImageLoader.Builder(context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    LaunchedEffect(bitmap) {
        bitmap?.let { btm ->
            isLoading.value = true
            pillDetector.processImage(
                bitmap = btm,
                onSuccess = { detections ->
                    detectedObjects.value = detections
                    viewModel.setDetectedPills(btm, detections)
                    isLoading.value = false
                },
                onFailure = {
                    isLoading.value = false
                }
            )
        }
    }

    LaunchedEffect(isSending, sendResult, sendError) {
        if (isSending || sendResult != null || sendError != null) {
            showResultSheet = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. 이미지 및 결과 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.1f)
                    .padding(horizontal = 20.dp, vertical = 24.dp)
                    .graphicsLayer {
                        shape = RoundedCornerShape(24.dp)
                        clip = true
                        shadowElevation = 15f
                        spotShadowColor = Color.Black.copy(alpha = 0.3f)
                    }
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                bitmap?.let { btm ->
                    Image(
                        bitmap = btm.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                    ResultCanvas(detections = detectedObjects.value)
                }

                if (isLoading.value) {
                    AsyncImage(
                        model = R.raw.result2,
                        contentDescription = "Loading",
                        imageLoader = imageLoader,
                        modifier = Modifier.size(150.dp)
                    )
                } else {
                    ResultCanvas(detections = detectedObjects.value)
                }

                IconButton(
                    onClick = {
                        navController.navigate("MainScreen") {
                            popUpTo("CameraScreen") { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 16.dp, top = 8.dp)
                        .align(Alignment.TopStart),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.White.copy(alpha = 0.8f),
                        contentColor = Color.Black
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home"
                    )
                }
            }

            // 2. 정보 및 버튼 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isLoading.value) "알약을 분석하고 있어요" else "분석 완료!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 검출 개수 표시
                Surface(
                    tonalElevation = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .then(if (isLoading.value) Modifier.shimmer() else Modifier),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isLoading.value) {
                            IconPlaceholder(size = 24.dp)
                        } else {
                            if (detectedObjects.value.isEmpty()) {
                                Icon(
                                    imageVector = Icons.Default.ReportProblem,
                                    contentDescription = null,
                                    tint = colorResource(R.color.RealRed)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        if (isLoading.value) {
                            TextPlaceholder(height = 20.dp, widthFraction = 0.6f)
                        } else {
                            Text(
                                text = if (detectedObjects.value.isEmpty()) "검출된 알약이 없습니다."
                                else "총 ${detectedObjects.value.size}개의 알약을 찾았습니다.",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // 3. 버튼 그룹
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Text("다시 찍기")
                    }

                    Button(
                        onClick = {
                            showResultSheet = true
                            viewModel.sendImage()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = detectedObjects.value.isNotEmpty() && !isLoading.value && !isSending,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text(
                            text = if (isSending) "전송 중..." else "결과 전송하기",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    if (showResultSheet) {
        ModalBottomSheet(
            onDismissRequest = { showResultSheet = false },
            sheetState = resultSheetState,
            containerColor = Color.White,
            tonalElevation = 0.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                when {
                    isSending -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            repeat(5) {
                                PillInfoBox(
                                    pillInfo = Pill(
                                        name = "",
                                        classification = "",
                                        manufacturer = "",
                                        pid = "",
                                        pillType = "",
                                        pillImg = ""
                                    ),
                                    isLoading = true
                                )
                            }
                        }
                    }

                    sendError != null -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 250.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(120.dp)
                                    .padding(bottom = 16.dp)
                            )
                            Text(
                                text = "데이터를 불러오지 못했습니다.",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            IconButton(
                                onClick = { viewModel.sendImage() },
                                modifier = Modifier.size(56.dp),
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = Color(0xFF6200EE),
                                    contentColor = Color.White
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "다시 전송하기"
                                )
                            }
                        }
                    }

                    !sendResult.isNullOrEmpty() -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 440.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(sendResult ?: emptyList()) { item ->
                                PillInfoBox(
                                    pillInfo = item.toPill(),
                                    onClick = {
                                        sheetScope.launch {
                                            resultSheetState.hide()
                                            showResultSheet = false
                                            navController.navigate("PillDetailScreen/${item.pillId}")
                                        }
                                    }
                                )
                            }
                        }
                    }

                    sendResult != null -> {
                        Text(
                            text = "인식된 알약 정보가 없습니다.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    else -> {
                        Text(
                            text = "결과 전송을 누르면 인식 결과가 표시됩니다.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun IconPlaceholder(size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(50))
            .background(Color.LightGray)
    )
}

@Composable
fun ResultCanvas(
    detections: List<PillDetection>
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasW = size.width
        val canvasH = size.height
        detections.forEach { pill ->
            val rect = pill.boundingBox
            val left = rect.left * canvasW
            val top = rect.top * canvasH
            val right = rect.right * canvasW
            val bottom = rect.bottom * canvasH

            drawRoundRect(
                color = Color.Green,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                cornerRadius = CornerRadius(8.dp.toPx()),
                style = Stroke(width = 4.dp.toPx())
            )
        }
    }
}

