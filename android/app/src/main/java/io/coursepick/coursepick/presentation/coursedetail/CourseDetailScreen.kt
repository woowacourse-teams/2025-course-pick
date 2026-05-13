package io.coursepick.coursepick.presentation.coursedetail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.presentation.toDistanceText

@Composable
fun CourseDetailScreen(
    courseDetail: CourseDetail,
    reviews: List<Review>,
    modifier: Modifier = Modifier,
) {
    var isFabVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection =
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (available.y < -1) {
                    isFabVisible = false
                } else if (available.y > 1) {
                    isFabVisible = true
                }

                return super.onPreScroll(available, source)
            }
        }

    Scaffold(floatingActionButton = {
        WriteReviewButton(
            onClick = { },
            isVisible = isFabVisible,
        )
    }) { innerPadding: PaddingValues ->
        Column(
            modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            CourseInfo(
                courseName = courseDetail.courseName,
                length = courseDetail.length,
                isFavorite = courseDetail.isFavorite,
                averageRating = courseDetail.averageRating,
            )

            Spacer(Modifier.height(10.dp))

            CourseReviewHeader(courseDetail.reviewCount)

            Spacer(Modifier.height(10.dp))

            CourseReviews(
                reviews = reviews,
                modifier =
                    Modifier
                        .weight(1F)
                        .nestedScroll(nestedScrollConnection),
            )
        }
    }
}

@Composable
private fun WriteReviewButton(
    onClick: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { fullHeight: Int -> fullHeight }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { fullHeight: Int -> fullHeight }) + fadeOut(),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onClick() }
                    .background(colorResource(R.color.point_primary))
                    .padding(horizontal = 18.dp, vertical = 10.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_write_review),
                contentDescription = null,
                tint = colorResource(R.color.item_white),
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = "리뷰 작성",
                color = colorResource(R.color.item_white),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun CourseInfo(
    courseName: CourseName,
    length: Length,
    averageRating: Float,
    isFavorite: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(if (isFavorite) R.drawable.icon_favorite else R.drawable.icon_not_favorite),
                contentDescription = null,
                tint = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .background(color = colorResource(R.color.background_tertiary), shape = CircleShape)
                        .padding(4.dp),
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = courseName.value,
                color = colorResource(R.color.item_primary),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = averageRating.toString(),
                color = colorResource(R.color.point_primary),
                fontSize = 18.sp,
            )

            Spacer(Modifier.width(10.dp))

            RatingStars(rating = averageRating, modifier = Modifier.height(18.dp))
        }

        Spacer(Modifier.height(10.dp))

        CourseLengthInfo(length = length)
    }
}

@Composable
private fun CourseLengthInfo(
    length: Length,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.icon_length),
            contentDescription = null,
            tint = colorResource(R.color.item_primary),
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = length.toDistanceText(),
            color = colorResource(R.color.item_primary),
            fontSize = 18.sp,
        )
    }
}

@Composable
private fun CourseReviewHeader(
    reviewCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Text(
            text = "리뷰",
            color = colorResource(R.color.item_primary),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(Modifier.width(5.dp))

        Text(
            text = "(${reviewCount}개)",
            color = colorResource(R.color.item_primary),
            fontSize = 16.sp,
        )

        Spacer(Modifier.width(10.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = colorResource(R.color.background_border),
        )
    }
}

@Composable
private fun CourseReviews(
    reviews: List<Review>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier) {
        itemsIndexed(reviews) { index: Int, review: Review ->
            ReviewItem(
                review = review,
                modifier = Modifier.padding(vertical = 10.dp),
            )

            if (index != reviews.lastIndex) {
                HorizontalDivider(thickness = 1.dp, color = colorResource(R.color.background_border_light))
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview() {
    CourseDetailScreen(
        courseDetail =
            CourseDetail(
                id = "",
                courseName = CourseName("석촌호수 동호"),
                length = Length(5678),
                averageRating = 4.32F,
                isFavorite = false,
                reviewCount = 99,
            ),
        reviews =
            List(10) { index: Int ->
                Review(
                    id = index.toString(),
                    username = "달리는 런숭이 $index",
                    isMine = index == 0,
                    rating = 4 + index / 10F,
                    comment = "리뷰 내용 ".repeat(10 + index * 5),
                )
            },
    )
}
