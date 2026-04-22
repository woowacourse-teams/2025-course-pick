package io.coursepick.coursepick.presentation.createcustomcourse

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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R

@Composable
fun SubmitCustomCourseDialog(
    courseName: String,
    onCourseNameChange: (courseName: String) -> Unit,
    isCourseNameOutOfBounds: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = onDismiss,
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
                text = stringResource(R.string.custom_course_submit_dialog_course_name_prompt_title),
                fontSize = 18.sp,
                color = colorResource(R.color.item_primary),
            )

            Spacer(Modifier.height(24.dp))

            TextField(
                value = courseName,
                onValueChange = onCourseNameChange,
                textStyle =
                    TextStyle(
                        fontSize = 16.sp,
                        color = colorResource(R.color.item_primary),
                    ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.custom_course_submit_dialog_course_name_placeholder),
                        fontSize = 16.sp,
                        color = colorResource(R.color.item_tertiary),
                    )
                },
                supportingText = {
                    if (isCourseNameOutOfBounds) Text(text = "코스 이름은 2~30자로 붙여주세요.")
                },
                isError = isCourseNameOutOfBounds,
                singleLine = true,
                shape = RoundedCornerShape(10),
                colors =
                    TextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.item_primary),
                        unfocusedTextColor = colorResource(R.color.item_tertiary),
                        errorTextColor = colorResource(R.color.item_primary),
                        focusedContainerColor = colorResource(R.color.background_primary),
                        unfocusedContainerColor = colorResource(R.color.background_tertiary),
                        errorContainerColor = colorResource(R.color.background_primary),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent
                    ),
                modifier = Modifier.focusRequester(focusRequester),
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
                        text = stringResource(R.string.custom_course_submit_dialog_cancel_button),
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
                        text = stringResource(R.string.custom_course_submit_dialog_confirm_button),
                        fontSize = 16.sp,
                        color = colorResource(R.color.item_primary),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SubmitCustomCourseDialogPreview_Normal() {
    SubmitCustomCourseDialog(
        courseName = "내가 만든 코스",
        onCourseNameChange = { },
        isCourseNameOutOfBounds = false,
        onDismiss = { },
        onConfirm = { },
    )
}

@PreviewLightDark
@Composable
private fun SubmitCustomCourseDialogPreview_Error() {
    SubmitCustomCourseDialog(
        courseName = "내가 만든 코스",
        onCourseNameChange = { },
        isCourseNameOutOfBounds = true,
        onDismiss = { },
        onConfirm = { },
    )
}
