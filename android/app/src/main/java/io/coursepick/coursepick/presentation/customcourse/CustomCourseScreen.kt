package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Coordinate
import io.coursepick.coursepick.domain.course.Course
import io.coursepick.coursepick.domain.course.CourseName
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.domain.course.InclineSummary
import io.coursepick.coursepick.domain.course.InclineType
import io.coursepick.coursepick.domain.course.Latitude
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.domain.course.Longitude
import io.coursepick.coursepick.domain.course.Segment

@Composable
fun CustomCourseScreen(uiState: CustomCourseUiState) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(color = colorResource(R.color.background_primary)),
    ) {
        Text(
            text = "나의 코스",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.item_primary),
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items = uiState.courses) { course: CustomCourseUiModel ->
                CustomCourseItem(course)
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CustomCourseScreenPreview() {
    CustomCourseScreen(
        uiState =
            CustomCourseUiState(
                courses = List(10) { CustomCourseUiModel.CUSTOM_COURSE_FIXTURE },
            ),
    )
}
