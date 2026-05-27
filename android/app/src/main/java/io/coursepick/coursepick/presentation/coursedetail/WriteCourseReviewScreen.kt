package io.coursepick.coursepick.presentation.coursedetail

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onComplete: () -> Unit,
    authViewModel: AuthViewModel = viewModel(),
    writeCourseReviewViewModel: WriteCourseReviewViewModel = viewModel(),
) {
    val context: Context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.uiEvent.collect { event: AuthUiEvent -> event.handle(context, writeCourseReviewViewModel) }
    }

    LaunchedEffect(Unit) {
        writeCourseReviewViewModel.event.collect { event: WriteCourseReviewViewModel.UiEvent -> event.handle(context, onComplete) }
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
        onSubmit = { writeCourseReviewViewModel.submitReview(courseDetail.id) },
        authDialog = writeCourseReviewViewModel.authDialog.collectAsStateWithLifecycle().value,
        onConfirmAuthDialog = { authViewModel.authenticate(KakaoAuthenticator(context), AuthFeature.SubmitReview(courseDetail.id)) },
        onDismissAuthDialog = writeCourseReviewViewModel::dismissAuthDialog,
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
    onComplete: () -> Unit,
) {
    when (this) {
        WriteCourseReviewViewModel.UiEvent.SubmitReviewSuccess -> {
            onComplete()
        }

        WriteCourseReviewViewModel.UiEvent.NoNetwork -> {
            Toast.makeText(context, context.getString(R.string.failure_no_network_toast_message), Toast.LENGTH_SHORT).show()
        }

        WriteCourseReviewViewModel.UiEvent.CourseAlreadyReviewed -> {
            Toast.makeText(context, context.getString(R.string.write_course_review_already_reviewed_message), Toast.LENGTH_SHORT).show()
        }

        WriteCourseReviewViewModel.UiEvent.InvalidReviewContent -> {
            Toast.makeText(context, context.getString(R.string.write_course_review_invalid_content), Toast.LENGTH_SHORT).show()
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
    onSubmit: () -> Unit,
    authDialog: AuthFeature?,
    onConfirmAuthDialog: () -> Unit,
    onDismissAuthDialog: () -> Unit,
) {
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
                isEnabled = canSubmit,
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth(),
            )

            if (authDialog != null) {
                AuthDialog(
                    feature = authDialog,
                    onDismissRequest = onDismissAuthDialog,
                    onKakaoLoginClick = onConfirmAuthDialog,
                )
            }
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
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = stringResource(R.string.write_course_review_submit_button),
        color = colorResource(R.color.item_white),
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        modifier =
            modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable(isEnabled) { onClick() }
                .background(colorResource(if (isEnabled) R.color.point_primary else R.color.item_tertiary))
                .padding(10.dp),
    )
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
        maxReviewLength = 1_000,
        canSubmit = true,
        onSubmit = { },
        authDialog = null,
        onConfirmAuthDialog = { },
        onDismissAuthDialog = { },
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
        maxReviewLength = 1_000,
        canSubmit = false,
        onSubmit = { },
        authDialog = null,
        onConfirmAuthDialog = { },
        onDismissAuthDialog = { },
    )
}
