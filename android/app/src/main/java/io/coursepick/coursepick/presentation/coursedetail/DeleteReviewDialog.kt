package io.coursepick.coursepick.presentation.coursedetail

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R

@Composable
fun DeleteReviewDialog(
    review: CourseReviewUiModel,
    onDismiss: () -> Unit,
    onConfirm: (CourseReviewUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        DeleteCourseDialogContent(
            review = review,
            onDismiss = onDismiss,
            onConfirm = onConfirm,
            modifier = modifier,
        )
    }
}

@Composable
private fun DeleteCourseDialogContent(
    review: CourseReviewUiModel,
    onDismiss: () -> Unit,
    onConfirm: (CourseReviewUiModel) -> Unit,
    modifier: Modifier = Modifier,
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
            text = stringResource(R.string.delete_review_dialog_title),
            color = colorResource(R.color.item_primary),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))

        Box(
            Modifier
                .fillMaxWidth()
                .background(colorResource(R.color.background_tertiary), RoundedCornerShape(10.dp))
                .padding(10.dp),
        ) {
            Text(
                text = review.content,
                color = colorResource(R.color.item_primary),
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 5,
            )
        }

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
                    text = stringResource(R.string.delete_review_dialog_negative_button),
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
                        .clickable { onConfirm(review) }
                        .background(colorResource(R.color.point_primary))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
            ) {
                Text(
                    text = stringResource(R.string.delete_review_dialog_positive_button),
                    fontSize = 16.sp,
                    color = colorResource(R.color.item_white),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ReviewCourseDialogPreview_ShortReview() {
    DeleteCourseDialogContent(
        review =
            CourseReviewUiModel(
                id = "",
                authorId = "",
                authorName = "달리는 런숭이",
                isMine = false,
                rating = 4.32F,
                content = "리뷰 내용",
            ),
        onDismiss = { },
        onConfirm = { },
    )
}

@PreviewLightDark
@Composable
private fun ReviewCourseDialogPreview_LongReview() {
    DeleteCourseDialogContent(
        review =
            CourseReviewUiModel(
                id = "",
                authorId = "",
                authorName = "달리는 런숭이",
                isMine = false,
                rating = 4.32F,
                content = "리뷰 내용 ".repeat(30),
            ),
        onDismiss = { },
        onConfirm = { },
    )
}
