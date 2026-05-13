package io.coursepick.coursepick.presentation.coursedetail

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

@Composable
fun ReviewItem(
    review: Review,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        ReviewItemHeading(
            username = review.username,
            isMine = review.isMine,
        )

        ReviewItemBody(
            rating = review.rating,
            comment = review.comment,
        )
    }
}

@Composable
private fun ReviewItemHeading(
    username: String,
    isMine: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = username,
            color = colorResource(R.color.item_primary),
            fontSize = 16.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(weight = 1F, fill = false),
        )

        if (isMine) {
            Spacer(Modifier.width(10.dp))

            MyReviewChip()
        }
    }
}

@Composable
fun ReviewItemBody(
    rating: Float?,
    comment: String?,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        if (rating != null) {
            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = rating.toString(),
                    color = colorResource(R.color.point_primary),
                    fontSize = 14.sp,
                )

                Spacer(Modifier.width(10.dp))

                RatingStars(rating = rating, modifier = Modifier.height(14.dp))
            }
        }

        if (comment != null) {
            Spacer(Modifier.height(10.dp))

            Text(
                text = comment,
                color = colorResource(R.color.item_primary),
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
private fun MyReviewChip(modifier: Modifier = Modifier) {
    Text(
        text = "내 리뷰",
        color = colorResource(R.color.item_primary),
        fontSize = 12.sp,
        modifier =
            modifier
                .border(width = 1.dp, color = colorResource(R.color.point_primary), shape = RoundedCornerShape(50))
                .padding(horizontal = 4.dp, vertical = 2.dp),
    )
}

@PreviewLightDark
@Composable
private fun ReviewItemPreview_IsMine_RatingOnly() {
    ReviewItem(
        review =
            Review(
                id = "",
                username = "달리는 런숭이",
                isMine = true,
                rating = 4.32F,
            ),
    )
}

@PreviewLightDark
@Composable
private fun ReviewItemPreview_IsMine_CommentOnly() {
    ReviewItem(
        review =
            Review(
                id = "",
                username = "달리는 런숭이",
                isMine = true,
                comment = "리뷰 내용 ".repeat(20),
            ),
    )
}

@PreviewLightDark
@Composable
private fun ReviewItemPreview_IsMine_RatingAndComment() {
    ReviewItem(
        review =
            Review(
                id = "",
                username = "달리는 런숭이",
                isMine = true,
                comment = "리뷰 내용 ".repeat(20),
                rating = 4.32F,
            ),
    )
}

@PreviewLightDark
@Composable
private fun ReviewItemPreview_IsNotMine() {
    ReviewItem(
        review =
            Review(
                id = "",
                username = "달리는 런숭이",
                isMine = false,
                comment = "리뷰 내용 ".repeat(20),
                rating = 4.32F,
            ),
    )
}
