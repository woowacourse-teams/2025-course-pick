package io.coursepick.coursepick.presentation.createcustomcourse

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R

@Composable
fun SnapGuideDialog(onDismiss: () -> Unit) {
    Dialog(onDismiss) {
        SnapGuideDialogContent(onDismiss)
    }
}

@Composable
private fun SnapGuideDialogContent(onDismiss: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .background(color = colorResource(R.color.background_primary), shape = RoundedCornerShape(10.dp))
                .padding(20.dp),
    ) {
        Text(
            text = stringResource(R.string.custom_course_snap_guide_dialog_message),
            color = colorResource(R.color.item_primary),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        Row {
            Image(
                painter = painterResource(R.drawable.image_custom_course_guide_snap_off),
                contentDescription = null,
                modifier =
                    Modifier
                        .weight(1F)
                        .clip(RoundedCornerShape(10.dp))
                        .border(width = 1.dp, color = colorResource(R.color.background_border), shape = RoundedCornerShape(10.dp)),
            )

            Spacer(Modifier.width(10.dp))

            Image(
                painter = painterResource(R.drawable.image_custom_course_guide_snap_on),
                contentDescription = null,
                modifier =
                    Modifier
                        .weight(1F)
                        .clip(RoundedCornerShape(10.dp))
                        .border(width = 1.dp, color = colorResource(R.color.background_border), shape = RoundedCornerShape(10.dp)),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.custom_course_snap_guide_dialog_dismiss_button),
            color = colorResource(R.color.item_primary),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(color = colorResource(R.color.background_tertiary))
                    .clickable { onDismiss() }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun SnapGuideDialogPreview() {
    SnapGuideDialogContent { }
}
