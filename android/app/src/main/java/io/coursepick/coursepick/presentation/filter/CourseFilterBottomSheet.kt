package io.coursepick.coursepick.presentation.filter

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.coursepick.coursepick.presentation.course.CoursesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFilterBottomSheet(
    coursesUiState: CoursesUiState,
    onDismissRequest: () -> Unit,
    onRangeSliderValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
    ) {
        CourseFilterContent(
            coursesUiState = coursesUiState,
            onRangeSliderValueChange = onRangeSliderValueChange,
        )
    }
}
