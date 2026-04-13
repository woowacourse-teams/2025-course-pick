package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R

@Composable
fun DiscardCustomCourseDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = { },
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                modifier
                    .clip(RoundedCornerShape(10))
                    .background(colorResource(R.color.background_primary))
                    .padding(20.dp),
        ) {
            Text(
                text = stringResource(R.string.custom_course_discard_dialog_title),
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.item_primary),
            )

            Spacer(Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .weight(1F)
                            .clip(RoundedCornerShape(50))
                            .clickable { onDismiss() }
                            .background(colorResource(R.color.background_tertiary))
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.custom_course_discard_dialog_cancel_button),
                        fontSize = 16.sp,
                        color = colorResource(R.color.item_tertiary),
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .weight(1F)
                            .clip(RoundedCornerShape(50))
                            .clickable { onConfirm() }
                            .background(colorResource(R.color.point_primary))
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                ) {
                    Text(
                        text = stringResource(R.string.custom_course_discard_dialog_confirm_button),
                        fontSize = 16.sp,
                        color = colorResource(R.color.item_white),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DiscardCustomCourseDialogPreview() {
    DiscardCustomCourseDialog(
        onDismiss = { },
        onConfirm = { },
    )
}
