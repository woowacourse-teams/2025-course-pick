package io.coursepick.coursepick.presentation.coursedetail

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.auth.AuthDialog
import io.coursepick.coursepick.presentation.auth.AuthFeature
import io.coursepick.coursepick.presentation.auth.AuthUiEvent
import io.coursepick.coursepick.presentation.auth.AuthViewModel
import io.coursepick.coursepick.presentation.auth.KakaoAuthenticator
import io.coursepick.coursepick.presentation.coursedetail.WriteCourseReviewViewModel.Companion.MAX_REVIEW_LENGTH

@Composable
fun WriteCourseReviewScreen(
    courseDetail: CourseDetailUiModel,
    exit: () -> Unit,
    complete: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    writeCourseReviewViewModel: WriteCourseReviewViewModel = viewModel(),
) {
    val context: Context = LocalContext.current

    BackHandler {
        writeCourseReviewViewModel.onExit()
    }

    LaunchedEffect(Unit) {
        authViewModel.uiEvent.collect { event: AuthUiEvent -> event.handle(context, writeCourseReviewViewModel) }
    }

    LaunchedEffect(Unit) {
        writeCourseReviewViewModel.event.collect { event: WriteCourseReviewViewModel.UiEvent -> event.handle(context, exit, complete) }
    }

    WriteCourseReviewScreen(
        courseName = courseDetail.name,
        length = courseDetail.length,
        rating = writeCourseReviewViewModel.rating.collectAsStateWithLifecycle().value ?: 0F,
        onSelectRating = writeCourseReviewViewModel::setRating,
        reviewContent = writeCourseReviewViewModel.reviewContent.collectAsStateWithLifecycle().value,
        onReviewContentChange = writeCourseReviewViewModel::setReviewText,
        maxReviewLength = MAX_REVIEW_LENGTH,
        canSubmit = writeCourseReviewViewModel.canSubmit.collectAsStateWithLifecycle().value,
        isSubmitting = writeCourseReviewViewModel.isSubmitting.collectAsStateWithLifecycle().value,
        onSubmit = { writeCourseReviewViewModel.submitReview(courseDetail.id) },
        authDialog = writeCourseReviewViewModel.authDialog.collectAsStateWithLifecycle().value,
        onConfirmAuthDialog = { authViewModel.authenticate(KakaoAuthenticator(context), AuthFeature.SubmitReview(courseDetail.id)) },
        onDismissAuthDialog = writeCourseReviewViewModel::dismissAuthDialog,
        showExitDialog = writeCourseReviewViewModel.showExitDialog.collectAsStateWithLifecycle().value,
        onConfirmExitDialog = writeCourseReviewViewModel::confirmExit,
        onDismissExitDialog = writeCourseReviewViewModel::dismissExitDialog,
    )
}

private fun AuthUiEvent.handle(
    context: Context,
    writeCourseReviewViewModel: WriteCourseReviewViewModel,
) {
    when (this) {
        is AuthUiEvent.AuthenticateSuccess -> {
            writeCourseReviewViewModel.onAuthSuccess(feature)
        }

        AuthUiEvent.AuthenticateFailure -> {
            Toast.makeText(context, context.getString(R.string.authentication_failure_message), Toast.LENGTH_SHORT).show()
        }
    }
}

private fun WriteCourseReviewViewModel.UiEvent.handle(
    context: Context,
    exit: () -> Unit,
    complete: () -> Unit,
) {
    when (this) {
        WriteCourseReviewViewModel.UiEvent.Exit -> {
            exit()
        }

        WriteCourseReviewViewModel.UiEvent.SubmitReviewSuccess -> {
            complete()
        }

        WriteCourseReviewViewModel.UiEvent.NoNetwork -> {
            Toast.makeText(context, context.getString(R.string.failure_no_network_toast_message), Toast.LENGTH_SHORT).show()
        }

        WriteCourseReviewViewModel.UiEvent.CourseAlreadyReviewed -> {
            Toast.makeText(context, context.getString(R.string.write_course_review_already_reviewed_message), Toast.LENGTH_SHORT).show()
        }

        WriteCourseReviewViewModel.UiEvent.NoRating -> {
            Toast.makeText(context, context.getString(R.string.write_course_review_no_rating_message), Toast.LENGTH_SHORT).show()
        }

        WriteCourseReviewViewModel.UiEvent.EmptyContent -> {
            Toast.makeText(context, context.getString(R.string.write_course_review_empty_content_message), Toast.LENGTH_SHORT).show()
        }

        WriteCourseReviewViewModel.UiEvent.UnknownFailure -> {
            Toast.makeText(context, context.getString(R.string.failure_unknown_toast_message), Toast.LENGTH_SHORT).show()
        }
    }
}

@Composable
fun WriteCourseReviewScreen(
    courseName: String,
    length: Double,
    rating: Float,
    onSelectRating: (Float) -> Unit,
    reviewContent: String,
    onReviewContentChange: (String) -> Unit,
    maxReviewLength: Int,
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    authDialog: AuthFeature?,
    onConfirmAuthDialog: () -> Unit,
    onDismissAuthDialog: () -> Unit,
    showExitDialog: Boolean,
    onConfirmExitDialog: () -> Unit,
    onDismissExitDialog: () -> Unit,
) {
    val focusManager: FocusManager = LocalFocusManager.current

    Scaffold { innerPadding: PaddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            CourseInfo(
                courseName = courseName,
                length = length,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))

            StarRating(
                rating = rating,
                starSize = 36.dp,
                onSelectRating = onSelectRating,
            )

            Spacer(Modifier.height(24.dp))

            ReviewTextField(
                value = reviewContent,
                onValueChange = onReviewContentChange,
                maxLength = maxReviewLength,
                modifier =
                    Modifier
                        .weight(weight = 1F, fill = false)
                        .fillMaxWidth(),
            )

            Spacer(Modifier.height(24.dp))

            SubmitReviewButton(
                canSubmit = canSubmit,
                isSubmitting = isSubmitting,
                onClick = {
                    focusManager.clearFocus()
                    onSubmit()
                },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (authDialog != null) {
            AuthDialog(
                feature = authDialog,
                onDismissRequest = onDismissAuthDialog,
                onKakaoLoginClick = onConfirmAuthDialog,
            )
        }

        if (showExitDialog) {
            DiscardReviewDialog(
                onDismiss = onDismissExitDialog,
                onConfirm = onConfirmExitDialog,
            )
        }
    }
}

@Composable
private fun CourseInfo(
    courseName: String,
    length: Double,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = courseName,
            color = colorResource(R.color.item_primary),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.height(10.dp))

        CourseLengthInfo(length)
    }
}

@Composable
private fun ReviewTextField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    modifier: Modifier = Modifier,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.write_course_review_review_content_placeholder),
                color = colorResource(R.color.item_tertiary),
                fontSize = 16.sp,
            )
        },
        supportingText = {
            Text(
                text = "${value.length}/$maxLength",
                textAlign = TextAlign.End,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
            )
        },
        colors =
            TextFieldDefaults.colors(
                focusedTextColor = colorResource(R.color.item_primary),
                unfocusedTextColor = colorResource(R.color.item_primary),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
        modifier =
            modifier
                .border(
                    width = 1.dp,
                    color = colorResource(R.color.background_border),
                    shape = RoundedCornerShape(8.dp),
                ),
    )
}

@Composable
private fun SubmitReviewButton(
    canSubmit: Boolean,
    isSubmitting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onClick() }
                .background(colorResource(if (canSubmit) R.color.point_primary else R.color.item_tertiary))
                .padding(10.dp),
    ) {
        Text(
            text = stringResource(R.string.write_course_review_submit_button),
            color = colorResource(R.color.item_white),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.alpha(if (isSubmitting) 0F else 1F),
        )

        CircularProgressIndicator(Modifier.alpha(if (isSubmitting) 1F else 0F))
    }
}

@Composable
private fun DiscardReviewDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
                text = stringResource(R.string.write_course_review_discard_dialog_title),
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
                        color = colorResource(R.color.item_primary),
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun WriteCourseReviewPreview_CanSubmit() {
    WriteCourseReviewScreen(
        courseName = "석촌호수 동호",
        length = 123.4,
        rating = 4F,
        onSelectRating = { },
        reviewContent = "리뷰 내용",
        onReviewContentChange = { },
        maxReviewLength = MAX_REVIEW_LENGTH,
        canSubmit = true,
        isSubmitting = false,
        onSubmit = { },
        authDialog = null,
        onConfirmAuthDialog = { },
        onDismissAuthDialog = { },
        showExitDialog = false,
        onConfirmExitDialog = { },
        onDismissExitDialog = { },
    )
}

@PreviewLightDark
@Composable
private fun WriteCourseReviewPreview_CannotSubmit() {
    WriteCourseReviewScreen(
        courseName = "석촌호수 동호",
        length = 123.4,
        rating = 0F,
        onSelectRating = { },
        reviewContent = "",
        onReviewContentChange = { },
        maxReviewLength = MAX_REVIEW_LENGTH,
        canSubmit = false,
        isSubmitting = false,
        onSubmit = { },
        authDialog = null,
        onConfirmAuthDialog = { },
        onDismissAuthDialog = { },
        showExitDialog = false,
        onConfirmExitDialog = { },
        onDismissExitDialog = { },
    )
}
