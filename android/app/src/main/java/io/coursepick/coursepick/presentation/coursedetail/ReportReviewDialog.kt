package io.coursepick.coursepick.presentation.coursedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R

@Composable
fun ReportReviewDialog(
    review: CourseReviewUiModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismiss) {
        ReportReviewDialogContent(
        review = review,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        modifier = modifier,
    )
    }
}

@Composable
private fun ReportReviewDialogContent(
    review: CourseReviewUiModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reportReasons: List<String> =
        listOf(
            stringResource(R.string.report_review_reason_promotional_content),
            stringResource(R.string.report_review_reason_offensive_content),
            stringResource(R.string.report_review_reason_irrelevant_content),
            stringResource(R.string.report_review_reason_miscellaneous),
        )

    var isConfirmEnabled by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .clip(RoundedCornerShape(10))
                .background(colorResource(R.color.background_primary))
                .padding(20.dp),
    ) {
        Text(
            text = stringResource(R.string.report_review_dialog_title, review.authorName),
            color = colorResource(R.color.item_primary),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(10.dp))

        ReportReasonDescription(reportReasons)

        ReportConfirmCheckbox(
            checked = isConfirmEnabled,
            onCheckChanged = { checked: Boolean -> isConfirmEnabled = checked },
            modifier = Modifier.align(Alignment.Start),
        )

        ReportReviewDialogButtons(
            isConfirmEnabled = isConfirmEnabled,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            modifier = Modifier.padding(10.dp),
        )
    }

}

@Composable
private fun ReportReasonDescription(
    reasons: List<String>,
    modifier: Modifier = Modifier,
) {
    Text(text = stringResource(R.string.report_review_dialog_description), color = colorResource(R.color.item_primary), fontSize = 16.sp)

    Spacer(Modifier.height(10.dp))

    Column(
        modifier
            .background(colorResource(R.color.background_tertiary), RoundedCornerShape(10.dp))
            .padding(10.dp),
    ) {
        reasons.forEach { reason: String ->
            Text(
                text = reason,
                color = colorResource(R.color.item_primary),
                fontSize = 14.sp,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Composable
private fun ReportConfirmCheckbox(
    checked: Boolean,
    onCheckChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckChanged,
            colors = CheckboxDefaults.colors(checkedColor = colorResource(R.color.point_primary)),
        )

        Text(
            text = stringResource(R.string.report_course_confirm_reason_description),
            color = colorResource(R.color.item_primary),
            fontSize = 14.sp,
            modifier =
                Modifier
                    .clickable { onCheckChanged(!checked) },
        )
    }
}

@Composable
private fun ReportReviewDialogButtons(
    isConfirmEnabled: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
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
                    .clickable { onDismiss() }
                    .background(colorResource(R.color.background_tertiary))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.report_course_dialog_negative_button),
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
                    .clickable(isConfirmEnabled) { onConfirm() }
                    .background(colorResource(if (isConfirmEnabled) R.color.point_primary else R.color.background_tertiary))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.report_course_dialog_positive_button),
                color = colorResource(if (isConfirmEnabled) R.color.item_primary else R.color.item_tertiary),
                fontSize = 16.sp,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ReportReviewDialogPreview() {
    ReportReviewDialogContent(
        review =
            CourseReviewUiModel(
                id = "",
                authorId = "",
                authorName = "달리는 런숭이",
                isMine = false,
                rating = 4.32F,
                content = "리뷰 내용 ".repeat(20),
            ),
        onConfirm = { },
        onDismiss = { },
    )
}
