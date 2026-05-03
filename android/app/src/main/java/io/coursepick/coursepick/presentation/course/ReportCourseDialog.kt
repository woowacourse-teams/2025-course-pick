package io.coursepick.coursepick.presentation.course

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
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude

@Composable
fun ReportCourseDialog(
    course: CourseItem,
    onConfirm: (CourseItem) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reportReasons: List<String> =
        listOf(
            stringResource(R.string.report_course_reason_duplicate_course),
            stringResource(R.string.report_course_reason_incorrect_course_data),
            stringResource(R.string.report_course_reason_cannot_access_course),
            stringResource(R.string.report_course_reason_incorrect_course_name),
        )

    var isConfirmEnabled by remember { mutableStateOf(false) }

    Dialog(onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                modifier
                    .clip(RoundedCornerShape(10))
                    .background(colorResource(R.color.background_primary))
                    .padding(20.dp),
        ) {
            Text(text = course.name, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(10.dp))

            ReportReasonDescription(reportReasons)

            ReportConfirmCheckbox(
                checked = isConfirmEnabled,
                onCheckChanged = { checked: Boolean -> isConfirmEnabled = checked },
                modifier = Modifier.align(Alignment.Start),
            )

            ReportCourseDialogButtons(
                isConfirmEnabled = isConfirmEnabled,
                onConfirm = { onConfirm(course) },
                onDismiss = onDismiss,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}

@Composable
private fun ReportReasonDescription(
    reasons: List<String>,
    modifier: Modifier = Modifier,
) {
    Text(text = stringResource(R.string.report_course_dialog_description), fontSize = 16.sp)

    Spacer(Modifier.height(10.dp))

    Text(
        text =
            buildAnnotatedString {
                withBulletList {
                    reasons.forEach { reason: String ->
                        withBulletListItem { append(reason) }
                    }
                }
            },
        fontSize = 14.sp,
        textAlign = TextAlign.Start,
        modifier =
            modifier
                .background(colorResource(R.color.background_tertiary), RoundedCornerShape(10.dp))
                .padding(10.dp),
    )
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
            fontSize = 14.sp,
            modifier =
                Modifier
                    .clickable { onCheckChanged(!checked) },
        )
    }
}

@Composable
private fun ReportCourseDialogButtons(
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
                text = stringResource(R.string.report_course_dialog_cancel_button),
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
                text = stringResource(R.string.report_course_dialog_confirm_button),
                fontSize = 16.sp,
                color = colorResource(if (isConfirmEnabled) R.color.item_primary else R.color.item_tertiary),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ReportCourseDialogPreview() {
    ReportCourseDialog(
        course =
            CourseItem(
                course =
                    Course(
                        id = "",
                        name = CourseName("석촌호수 동호 한바퀴"),
                        distance = Distance(0),
                        length = Length(0),
                        coordinates = List(2) { (Coordinate(Latitude(0.0), Longitude(0.0))) },
                    ),
                selected = false,
                favorite = false,
            ),
        onConfirm = { },
        onDismiss = { },
    )
}
