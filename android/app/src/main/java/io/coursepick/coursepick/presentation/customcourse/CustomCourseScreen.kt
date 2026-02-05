package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun CustomCourseScreen(
    uiState: CustomCourseUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(R.color.background_primary)),
        ) {
            Text(
                text = stringResource(R.string.custom_courses_header),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.item_primary),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            )
            LazyColumn(
                contentPadding = PaddingValues(bottom = 60.dp),
                modifier =
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(rememberNestedScrollInteropConnection()),
            ) {
                items(items = uiState.courses) { course: CustomCourseUiModel ->
                    CustomCourseItem(course)
                }
            }
        }

        FloatingActionButton(
            onClick = {},
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
fun CustomCourseScreenPreview() {
    CustomCourseScreen(
        uiState =
            CustomCourseUiState(
                courses =
                    List(10) { index: Int ->
                        CustomCourseUiModel(
                            course =
                                Course(
                                    id = "$index",
                                    name = CourseName("Preview Course $index"),
                                    distance = Distance(123),
                                    length = Length(456),
                                    coordinates =
                                        listOf(
                                            Coordinate(Latitude(0.0), Longitude(0.0)),
                                            Coordinate(Latitude(0.0), Longitude(0.0)),
                                        ),
                                ),
                            selected = false,
                        )
                    },
            ),
    )
}
