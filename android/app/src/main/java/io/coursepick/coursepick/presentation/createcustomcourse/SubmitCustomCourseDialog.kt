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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R

@Composable
fun SubmitCustomCourseDialog(
    courseName: String,
    onCourseNameChange: (courseName: String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager: FocusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

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
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                singleLine = true,
                shape = RoundedCornerShape(10),
                colors =
                    TextFieldDefaults.colors(
                        focusedTextColor = colorResource(R.color.item_primary),
                        unfocusedTextColor = colorResource(R.color.item_tertiary),
                        focusedContainerColor = colorResource(R.color.background_primary),
                        unfocusedContainerColor = colorResource(R.color.background_tertiary),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
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
                        color = colorResource(R.color.item_white),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SubmitCustomCourseDialogPreview() {
    SubmitCustomCourseDialog(
        courseName = "내가 만든 코스",
        onCourseNameChange = { },
        onDismiss = { },
        onConfirm = { },
    )
}
