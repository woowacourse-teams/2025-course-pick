package io.coursepick.coursepick.presentation.coursedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    modifier: Modifier = Modifier,
) {
    Scaffold { innerPadding: PaddingValues ->
        Column(
            modifier
                .padding(innerPadding)
                .padding(10.dp),
        ) {
            CourseInfo(
                courseName = courseDetail.courseName,
                length = courseDetail.length,
                isFavorite = courseDetail.isFavorite,
                averageRating = courseDetail.averageRating,
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

@PreviewLightDark
@Composable
private fun CourseDetailScreenPreview() {
    CourseDetailScreen(
        courseDetail =
            CourseDetail(
                id = "",
                courseName = CourseName("석촌호수 동호"),
                length = Length(5678),
                isFavorite = false,
                reviewCount = 99,
                averageRating = 4.32F,
            ),
    )
}
