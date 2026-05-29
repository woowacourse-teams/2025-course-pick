package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.presentation.customcourse.component.CourseDistanceChip
import io.coursepick.coursepick.presentation.customcourse.component.CourseLengthChip
import io.coursepick.coursepick.presentation.customcourse.component.CourseNavigationButton

@Composable
fun CustomCourseItemCard(
    customCourse: CustomCourseItem,
    onSelect: () -> Unit,
    onNavigateToCourse: () -> Unit,
    onNavigateToDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor: Color =
        if (customCourse.selected) {
            colorResource(R.color.background_tertiary)
        } else {
            colorResource(R.color.background_primary)
        }

    val horizontalDividerColor: Color =
        if (customCourse.selected) backgroundColor else colorResource(R.color.background_border_light)

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .clickable { onSelect() }
                    .padding(vertical = 10.dp)
                    .padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier =
                    Modifier.weight(1f),
            ) {
                Text(
                    text = customCourse.name,
                    color = colorResource(R.color.item_primary),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row {
                    CourseLengthChip(length = customCourse.length)
                    if (customCourse.distance != null) {
                        Spacer(modifier = Modifier.width(10.dp))
                        CourseDistanceChip(distance = customCourse.distance)
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            CourseNavigationButton(onClick = onNavigateToCourse)

            Icon(
                painter = painterResource(R.drawable.icon_course_detail),
                contentDescription = stringResource(R.string.course_item_navigate_to_detail_button_description),
                tint = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onNavigateToDetail() }
                        .padding(12.dp),
            )
        }

        HorizontalDivider(
            modifier =
                Modifier
                    .fillMaxWidth(),
            thickness = 1.dp,
            color = horizontalDividerColor,
        )
    }
}

@PreviewLightDark
@Composable
private fun CustomCourseItemCardPreview() {
    val course =
        Course(
            id = "1",
            name = CourseName("건대입구-잠실대교-종합운동장건대입구-잠실대교-종합운동장"),
            distance = Distance(500),
            length = Length(5000),
            coordinates =
                listOf(
                    Coordinate(Latitude(37.5400), Longitude(127.0700)),
                    Coordinate(Latitude(37.5450), Longitude(127.0750)),
                    Coordinate(Latitude(37.5500), Longitude(127.0800)),
                ),
        )
    CustomCourseItemCard(
        customCourse =
            CustomCourseItem(
                course = course,
                selected = false,
            ),
        onSelect = { },
        onNavigateToCourse = { },
        onNavigateToDetail = { },
        modifier = Modifier,
    )
}
