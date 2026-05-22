package io.coursepick.coursepick.presentation.coursedetail

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.presentation.coursedetail.WriteCourseReviewViewModel.Companion.MAX_REVIEW_LENGTH

@Composable
fun WriteCourseReviewScreen(viewModel: WriteCourseReviewViewModel = viewModel()) {
    WriteCourseReviewScreen(
        courseName = viewModel.courseName.collectAsStateWithLifecycle().value,
        length = viewModel.courseLength.collectAsStateWithLifecycle().value,
        rating = viewModel.rating.collectAsStateWithLifecycle().value,
        onSelectRating = viewModel::setRating,
        reviewContent = viewModel.reviewContent.collectAsStateWithLifecycle().value,
        onReviewContentChange = viewModel::setReviewText,
        maxReviewLength = MAX_REVIEW_LENGTH,
        canSubmit = viewModel.canSubmit.collectAsStateWithLifecycle().value,
    )
}

@Composable
fun WriteCourseReviewScreen(
    courseName: CourseName,
    length: Length,
    rating: Float,
    onSelectRating: (Float) -> Unit,
    reviewContent: String,
    onReviewContentChange: (String) -> Unit,
    maxReviewLength: Int,
    canSubmit: Boolean,
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
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun CourseInfo(
    courseName: CourseName,
    length: Length,
    modifier: Modifier = Modifier,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Text(
            text = courseName.value,
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
                text = "코스에 대한 리뷰를 남겨주세요.",
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
        text = "작성 완료",
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
        courseName = CourseName("석촌호수 동호"),
        length = Length(5678),
        rating = 4F,
        onSelectRating = { },
        reviewContent = "리뷰 내용",
        onReviewContentChange = { },
        maxReviewLength = 1_000,
        canSubmit = true,
    )
}

@PreviewLightDark
@Composable
private fun WriteCourseReviewPreview_CannotSubmit() {
    WriteCourseReviewScreen(
        courseName = CourseName("석촌호수 동호"),
        length = Length(5678),
        rating = 0F,
        onSelectRating = { },
        reviewContent = "",
        onReviewContentChange = { },
        maxReviewLength = 1_000,
        canSubmit = false,
    )
}
