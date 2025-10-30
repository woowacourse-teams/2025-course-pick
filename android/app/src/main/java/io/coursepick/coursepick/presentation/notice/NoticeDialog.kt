package io.coursepick.coursepick.presentation.notice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import io.coursepick.coursepick.R

/**
 * 공지사항 다이얼로그
 *
 * @param imageUrl 표시할 이미지 URL
 * @param title 다이얼로그 제목
 * @param description 다이얼로그 설명
 * @param onDismissRequest 다이얼로그가 닫힐 때 호출되는 콜백
 * @param onDoNotShowAgain "다시 보지 않음" 버튼 클릭 시 호출되는 콜백
 */
@Composable
fun NoticeDialog(
    imageUrl: String,
    title: String,
    description: String,
    onDismissRequest: () -> Unit,
    onDoNotShowAgain: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = colorResource(R.color.background_primary)),
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(R.color.gray4),
            )

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider()

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
            ) {
                TextButton(
                    onClick = {
                        onDoNotShowAgain()
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.notice_dialog_do_not_show_again),
                        color = colorResource(R.color.item_primary),
                    )
                }

                VerticalDivider(modifier = Modifier.fillMaxHeight())

                TextButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.notice_dialog_close),
                        color = colorResource(R.color.item_primary),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NoticeDialogPreview() {
    NoticeDialog(
        imageUrl = "",
        title =
            "강남·송파 코스는 저희가 검증했어요\n" +
                " 다른 지역은 아직 검증 중이에요 \uD83C\uDFC3",
        description = "* 메뉴 탭에서 다시 확인할 수 있어요.",
        onDismissRequest = {},
        onDoNotShowAgain = {},
    )
}
