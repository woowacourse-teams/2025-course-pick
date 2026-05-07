package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.presentation.customcourse.component.EmptyDescription
import io.coursepick.coursepick.presentation.customcourse.component.Header

@Composable
fun CustomCourseScreen(
    customCourses: List<Course>,
    onGoToCreateCustomCourse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(colorResource(R.color.background_primary)),
            ) {
                Header(stringResource(R.string.custom_courses_header))
            }
            if (customCourses.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    EmptyDescription(text = stringResource(R.string.empty_custom_courses_description))
                }
            } else {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(
                        items = customCourses,
                        key = { customCourse -> customCourse.id },
                    ) { customCourse ->
                        CustomCourseItem(
                            customCourse = customCourse,
                            onSelect = { },
                            onNavigateToCourse = { },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = onGoToCreateCustomCourse,
            shape = CircleShape,
            containerColor = colorResource(R.color.point_primary),
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
                    .size(50.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(30.dp),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun CustomCourseScreen_EmptyPreview() {
    CustomCourseScreen(
        customCourses = emptyList(),
        onGoToCreateCustomCourse = { },
    )
}

@PreviewLightDark
@Composable
private fun CustomCourseScreen_WithCoursesPreview() {
    val customCourse: List<Course> =
        List(10) { index ->
            Course(
                id = index.toString(),
                name = CourseName("건대입구-잠실대교-종합운동장 ${index + 1}"),
                distance = Distance(10),
                length = Length(100),
                coordinates =
                    listOf(
                        Coordinate(Latitude(1.0), Longitude(1.0)),
                        Coordinate(
                            Latitude(1.0 + 0.0001),
                            Longitude(1.0 + 0.0001),
                        ),
                    ),
            )
        }

    CustomCourseScreen(
        customCourses = customCourse,
        onGoToCreateCustomCourse = { },
    )
}
