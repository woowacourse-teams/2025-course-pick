package io.coursepick.coursepick.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@Composable
fun LoginDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = colorResource(R.color.background_primary))
                    .padding(20.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.align(Alignment.TopEnd), // 오른쪽 위 고정
                tint = colorResource(R.color.item_primary),
            )

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                // X 버튼 공간 확보
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.image_coursepick_mascot),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "즐겨찾기는 로그인이\n필요한 서비스입니다.",
                    fontSize = 16.sp,
                    color = colorResource(R.color.item_primary),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.image_kakao_login_large),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun LoginDialogPreview() {
    CoursePickTheme {
        LoginDialog(
            onDismissRequest = { },
        )
    }
}
