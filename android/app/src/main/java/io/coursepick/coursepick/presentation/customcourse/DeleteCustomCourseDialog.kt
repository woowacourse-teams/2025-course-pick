package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R

@Composable
fun DeleteCustomCourseDialog(
    state: DeleteCourseDialogState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Dialog(onDismissRequest = { if (!state.isDeleting) onDismiss() }) {
        DeleteCustomCourseDialogContent(
            state = state,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
        )
    }
}

@Composable
private fun DeleteCustomCourseDialogContent(
    state: DeleteCourseDialogState,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            Modifier
                .clip(RoundedCornerShape(10))
                .background(colorResource(R.color.background_primary))
                .padding(20.dp),
    ) {
        Text(
            text = state.courseName,
            color = colorResource(R.color.item_primary),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.delete_custom_course_dialog_title),
            color = colorResource(R.color.item_primary),
            fontSize = 16.sp,
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.delete_custom_course_dialog_warning),
            color = colorResource(R.color.item_secondary),
            fontSize = 14.sp,
        )

        DeleteCustomCourseDialogButtons(
            isDeleting = state.isDeleting,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            modifier = Modifier.padding(10.dp),
        )
    }
}

@Composable
private fun DeleteCustomCourseDialogButtons(
    isDeleting: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .weight(1F)
                    .clip(RoundedCornerShape(50))
                    .clickable(enabled = !isDeleting) { onDismiss() }
                    .background(colorResource(R.color.background_tertiary))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.delete_custom_course_dialog_negative_button),
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
                    .clickable(enabled = !isDeleting) { onConfirm() }
                    .background(colorResource(if (!isDeleting) R.color.point_primary else R.color.background_tertiary))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.delete_custom_course_dialog_positive_button),
                color = colorResource(R.color.item_primary),
                fontSize = 16.sp,
                modifier = Modifier.alpha(if (isDeleting) 0F else 1F),
            )

            CircularProgressIndicator(
                color = colorResource(R.color.item_primary),
                strokeWidth = 2.dp,
                modifier =
                    Modifier
                        .size(16.dp)
                        .alpha(if (isDeleting) 1F else 0F),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun DeleteCustomCourseDialogPreview() {
    DeleteCustomCourseDialogContent(
        state = DeleteCourseDialogState("", "석촌호수 한바퀴", false),
        onDismiss = { },
        onConfirm = { },
    )
}

@PreviewLightDark
@Composable
private fun DeleteCustomCourseDialogDeletingPreview() {
    DeleteCustomCourseDialogContent(
        state = DeleteCourseDialogState("", "석촌호수 한바퀴", true),
        onDismiss = { },
        onConfirm = { },
    )
}
