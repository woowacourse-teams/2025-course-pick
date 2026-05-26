package io.coursepick.coursepick.presentation.coursedetail

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

@Composable
fun ReviewItem(
    review: CourseReviewUiModel,
    onDelete: (CourseReviewUiModel) -> Unit,
    onReport: (CourseReviewUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        ReviewItemHeader(
            username = review.authorName,
            isMine = review.isMine,
            onDelete = { onDelete(review) },
            onReport = { onReport(review) },
        )

        ReviewItemBody(
            rating = review.rating,
            comment = review.content,
        )
    }
}

@Composable
private fun ReviewItemHeader(
    username: String,
    isMine: Boolean,
    onDelete: () -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1F),
        ) {
            Text(
                text = username,
                color = colorResource(R.color.item_primary),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(weight = 1F, fill = false),
            )

            if (isMine) {
                Spacer(Modifier.width(10.dp))

                MyReviewChip()
            }
        }

        ReviewActionButton(
            isMine = isMine,
            onDelete = onDelete,
            onReport = onReport,
        )
    }
}

@Composable
private fun MyReviewChip(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.review_item_my_review_chip),
        color = colorResource(R.color.item_primary),
        fontSize = 12.sp,
        modifier =
            modifier
                .border(width = 1.dp, color = colorResource(R.color.point_primary), shape = RoundedCornerShape(50))
                .padding(horizontal = 8.dp),
    )
}

@Composable
private fun ReviewActionButton(
    isMine: Boolean,
    onDelete: () -> Unit,
    onReport: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        var expanded by remember { mutableStateOf(false) }

        Icon(
            painter = painterResource(R.drawable.icon_review_action),
            contentDescription = stringResource(R.string.course_detail_review_menu_description),
            tint = colorResource(R.color.item_primary),
            modifier =
                Modifier
                    .clip(CircleShape)
                    .clickable { expanded = true }
                    .padding(4.dp),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            shape = RoundedCornerShape(10.dp),
            containerColor = colorResource(R.color.background_secondary),
        ) {
            if (isMine) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.review_item_action_delete),
                            color = colorResource(R.color.item_primary),
                            fontSize = 14.sp,
                        )
                    },
                    onClick = onDelete,
                )
            }

            if (!isMine) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.review_item_action_report),
                            color = colorResource(R.color.item_primary),
                            fontSize = 14.sp,
                        )
                    },
                    onClick = onReport,
                )
            }
        }
    }
}

@Composable
private fun ReviewItemBody(
    rating: Float,
    comment: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = rating.toString(),
                color = colorResource(R.color.point_primary),
                fontSize = 14.sp,
            )

            Spacer(Modifier.width(10.dp))

            StarRating(rating = rating, starSize = 14.dp)
        }

        Spacer(Modifier.height(4.dp))

        Text(
            text = comment,
            color = colorResource(R.color.item_primary),
            fontSize = 14.sp,
        )
    }
}

@PreviewLightDark
@Composable
private fun ReviewItemPreview_IsMine() {
    ReviewItem(
        review =
            CourseReviewUiModel(
                id = "",
                authorId = "",
                authorName = "달리는 런숭이",
                isMine = true,
                rating = 4.32F,
                content = "리뷰 내용 ".repeat(20),
            ),
        onDelete = { },
        onReport = { },
    )
}

@PreviewLightDark
@Composable
private fun ReviewItemPreview_IsNotMine() {
    ReviewItem(
        review =
            CourseReviewUiModel(
                id = "",
                authorId = "",
                authorName = "달리는 런숭이",
                isMine = false,
                rating = 4.32F,
                content = "리뷰 내용 ".repeat(20),
            ),
        onDelete = { },
        onReport = { },
    )
}
