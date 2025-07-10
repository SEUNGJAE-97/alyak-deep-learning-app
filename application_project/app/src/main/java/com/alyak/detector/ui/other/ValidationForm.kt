package com.alyak.detector.ui.other

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alyak.detector.R
import com.alyak.detector.ui.components.ContentBox
import com.alyak.detector.ui.components.CustomUnderlineTextField

@Composable
fun ValidationForm() {
    var email by remember { mutableStateOf("") }

    ContentBox {
        Column {
            Text(
                text = "이메일",
                color = colorResource(R.color.primaryBlue)
            )

            Spacer(modifier = Modifier.height(30.dp))

            CustomUnderlineTextField(
                value = email,
                onValueChange = { email = it },
                hint = "email",
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.check),
                        modifier = Modifier.size(24.dp),
                        contentDescription = "check email state"
                    )
                }
            )

            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "인증번호",
                color = colorResource(R.color.primaryBlue)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier
            ){

            }

        }
    }

}


@Composable
@Preview(showBackground = true)
fun ValidationFormPreview() {
    ValidationForm()
}