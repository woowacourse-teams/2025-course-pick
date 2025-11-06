package io.coursepick.coursepick.presentation.notice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.notice.Notice
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@Composable
fun NoticeDialog(
    notice: Notice,
    onDismissRequest: () -> Unit,
    onDoNotShowAgain: (id: String) -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = colorResource(R.color.background_primary))
                    .padding(top = 20.dp, start = 20.dp, end = 20.dp),
        ) {
            AsyncImage(
                model = notice.imageUrl,
                contentDescription = null,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = notice.title,
                fontSize = 16.sp,
                color = colorResource(R.color.item_primary),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = notice.description,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = colorResource(R.color.item_secondary),
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
            ) {
                TextButton(
                    onClick = {
                        onDoNotShowAgain(notice.id)
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = stringResource(R.string.notice_dialog_do_not_show_again),
                        color = colorResource(R.color.item_primary),
                    )
                }

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

@PreviewLightDark()
@Composable
private fun NoticeDialogPreview() {
    CoursePickTheme {
        NoticeDialog(
            notice =
                Notice(
                    id = "",
                    imageUrl = "",
                    title =
                        "강남·송파 코스는 저희가 검증했어요\n" +
                            "다른 지역은 아직 검증 중이에요 🏃",
                    description = "* 메뉴 탭에서 다시 확인할 수 있어요.",
                ),
            onDismissRequest = {},
            onDoNotShowAgain = {},
        )
    }
}
