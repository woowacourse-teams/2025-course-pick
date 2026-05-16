package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
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
import io.coursepick.coursepick.presentation.course.UiStatus
import io.coursepick.coursepick.presentation.customcourse.component.EmptyDescription
import io.coursepick.coursepick.presentation.customcourse.component.Header
import io.coursepick.coursepick.presentation.customcourse.component.LoadingIndicator

@Composable
fun CustomCourseScreen(
    modifier: Modifier = Modifier,
    status: CustomCourseUiState,
    onReconnect: () -> Unit,
    onGoToCreateCustomCourse: () -> Unit,
    onSelect: (CustomCourseItem) -> Unit,
    onNavigateToCourse: (CustomCourseItem) -> Unit,
) {
    val nestedScrollInterop = rememberNestedScrollInteropConnection()

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .nestedScroll(nestedScrollInterop),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Header(stringResource(R.string.custom_courses_header))

            when (status.status) {
                UiStatus.Success -> {
                    if (status.customCourses.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            EmptyDescription(text = stringResource(R.string.empty_custom_courses_description))
                        }
                    } else {
                        LazyColumn(
                            state = rememberLazyListState(),
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .nestedScroll(nestedScrollInterop),
                            contentPadding = PaddingValues(bottom = 50.dp),
                        ) {
                            items(
                                items = status.customCourses,
                                key = CustomCourseItem::id,
                            ) { customCourse: CustomCourseItem ->
                                CustomCourseItemCard(
                                    customCourse = customCourse,
                                    onSelect = { onSelect(customCourse) },
                                    onNavigateToCourse = { onNavigateToCourse(customCourse) },
                                    modifier = Modifier.animateItem(),
                                )
                            }
                        }
                    }
                }

                UiStatus.NoInternet -> NetworkErrorView(onReconnect)

                UiStatus.Failure -> EmptyDescription(text = stringResource(R.string.custom_courses_load_failed))

                UiStatus.Loading -> LoadingIndicator()
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
    val status =
        CustomCourseUiState(
            status = UiStatus.Success,
            customCourses = emptyList(),
        )

    CustomCourseScreen(
        status = status,
        onReconnect = {},
        onGoToCreateCustomCourse = { },
        onSelect = { },
        onNavigateToCourse = { },
    )
}

@PreviewLightDark
@Composable
private fun CustomCourseScreen_WithCoursesPreview() {
    val customCourse: List<CustomCourseItem> =
        List(10) { index: Int ->
            CustomCourseItem(
                course =
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
                    ),
                selected = false,
            )
        }

    val status =
        CustomCourseUiState(
            status = UiStatus.Success,
            customCourses = customCourse,
        )

    CustomCourseScreen(
        status = status,
        onReconnect = {},
        onGoToCreateCustomCourse = { },
        onSelect = { },
        onNavigateToCourse = { },
    )
}

@PreviewLightDark
@Composable
private fun CustomCourseScreen_LoadingPreview() {
    val status =
        CustomCourseUiState(
            status = UiStatus.Loading,
            customCourses = emptyList(),
        )
    CustomCourseScreen(
        status = status,
        onReconnect = {},
        onGoToCreateCustomCourse = { },
        onSelect = { },
        onNavigateToCourse = { },
    )
}

@PreviewLightDark
@Composable
private fun CustomCourseScreen_NoInternetPreview() {
    val status =
        CustomCourseUiState(
            status = UiStatus.NoInternet,
            customCourses = emptyList(),
        )
    CustomCourseScreen(
        status = status,
        onReconnect = {},
        onGoToCreateCustomCourse = { },
        onSelect = { },
        onNavigateToCourse = { },
    )
}

@PreviewLightDark
@Composable
private fun CustomCourseScreen_FailurePreview() {
    val status =
        CustomCourseUiState(
            status = UiStatus.Failure,
            customCourses = emptyList(),
        )
    CustomCourseScreen(
        status = status,
        onReconnect = {},
        onGoToCreateCustomCourse = { },
        onSelect = { },
        onNavigateToCourse = { },
    )
}
