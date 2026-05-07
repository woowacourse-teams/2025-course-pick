package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
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
import io.coursepick.coursepick.presentation.component.CourseDistanceChip
import io.coursepick.coursepick.presentation.component.CourseLengthChip
import io.coursepick.coursepick.presentation.component.CourseNavigationButton

@Composable
fun CustomCourseItem(
    customCourse: Course,
    onSelect: () -> Unit,
    onNavigateToCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth(),
    ) {
        Box(
            modifier =
                modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.background_primary))
                    .clickable { onSelect() }
                    .padding(vertical = 10.dp, horizontal = 20.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(end = 70.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = customCourse.name.value,
                        color = colorResource(R.color.item_primary),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    CourseLengthChip(length = customCourse.length)

                    if (customCourse.distance != null) {
                        Spacer(modifier = Modifier.width(10.dp))

                        CourseDistanceChip(distance = customCourse.distance)
                    }
                }
            }
            CourseNavigationButton(
                onClick = { onNavigateToCourse() },
                modifier =
                    Modifier
                        .align(Alignment.CenterEnd),
            )
        }

        HorizontalDivider(
            modifier =
                Modifier
                    .fillMaxWidth(),
            thickness = 1.dp,
            color = colorResource(R.color.background_border_light),
        )
    }
}

@PreviewLightDark
@Composable
fun CustomCourseItemPreview() {
    CustomCourseItem(
        customCourse =
            Course(
                id = "1",
                name = CourseName("건대입구-잠실대교-종합운동장"),
                distance = Distance(500),
                length = Length(5000),
                coordinates =
                    listOf(
                        Coordinate(Latitude(37.5400), Longitude(127.0700)),
                        Coordinate(Latitude(37.5450), Longitude(127.0750)),
                        Coordinate(Latitude(37.5500), Longitude(127.0800)),
                    ),
            ),
        onSelect = {},
        onNavigateToCourse = {},
        modifier = Modifier,
    )
}
