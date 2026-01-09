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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

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
                modifier = Modifier.size(40.dp),
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
                courses = List(10) { CustomCourseUiModel.CUSTOM_COURSE_FIXTURE },
            ),
    )
}
