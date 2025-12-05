package io.coursepick.coursepick.presentation.filter

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.coursepick.coursepick.presentation.course.CoursesUiState
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFilterBottomSheet(
    coursesUiState: CoursesUiState,
    onDismissRequest: () -> Unit,
    onFilterAction: (CourseFilterAction) -> Unit,
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
            onFilterAction = onFilterAction,
        )
    }
}

@PreviewLightDark
@Composable
private fun CourseFilterBottomSheetPreview() {
    CoursePickTheme {
        var showSheet by remember { mutableStateOf(true) }

        if (showSheet) {
            CourseFilterBottomSheet(
                coursesUiState =
                    CoursesUiState(
                        originalCourses = listOf(),
                    ),
                onDismissRequest = { showSheet = false },
                onFilterAction = {},
            )
        }
    }
}
