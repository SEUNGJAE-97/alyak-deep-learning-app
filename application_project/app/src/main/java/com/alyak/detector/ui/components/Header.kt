package com.alyak.detector.ui.components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alyak.detector.R
import com.alyak.detector.feature.family.ui.main.MainViewModel

@Composable
fun HeaderForm(
    name: String,
    onNotificationClick: () -> Unit = {},
    viewModel: MainViewModel = hiltViewModel()
) {
    val unreadCount by viewModel.unreadNotificationCount.collectAsState()

    HeaderFormContent(
        name = name,
        hasNewNotification = unreadCount > 0,
        onNotificationClick = onNotificationClick,
    )
}

@Composable
fun HeaderFormContent(
    name: String,
    hasNewNotification: Boolean,
    onNotificationClick: () -> Unit = {},
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splash),
                    contentDescription = "Splash Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "안녕하세요, ${name}님",
                    fontSize = 15.sp,
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                NotificationBellAnimation(
                    isPlaying = hasNewNotification,
                    onClick = {
                        Log.d("HeaderForm", "종 아이콘 클릭됨!")
                        onNotificationClick()
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))
                Image(
                    painter = painterResource(R.drawable.dummy_profile),
                    contentDescription = "사용자의 프로필 사진",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            thickness = 0.5.dp,
            color = colorResource(R.color.lightGray)
        )
    }
}

@Composable
fun NotificationBellAnimation(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.alarm_bell))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying,
        iterations = LottieConstants.IterateForever
    )

    LottieAnimation(
        composition = composition,
        progress = { if (isPlaying) progress else 0f },
        modifier = Modifier
            .size(40.dp)
            .clickable { onClick() }
    )

}

@Composable
@Preview(showBackground = true)
fun HeaderFormPreview() {
    HeaderFormContent(
        name = "김민수",
        hasNewNotification = true,
    )
}
