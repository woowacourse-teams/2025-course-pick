package io.coursepick.coursepick.presentation.filter

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.coursepick.coursepick.presentation.course.CoursesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFilterBottomSheet(
    coursesUiState: CoursesUiState,
    onDismissRequest: () -> Unit,
    onRangeSliderValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        CourseFilterContent(
            coursesUiState = coursesUiState,
            onRangeSliderValueChange = onRangeSliderValueChange,
            onCancel = onCancel,
        )
    }
}
