package io.coursepick.coursepick.presentation.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetState
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.course.CoursesUiState
import io.coursepick.coursepick.presentation.model.Difficulty
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseFilterBottomSheet(
    coursesUiState: CoursesUiState,
    onDismissRequest: () -> Unit,
    onFilterAction: (CourseFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(R.string.filter_dialog_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.item_primary),
                )
                Text(
                    text = stringResource(R.string.filter_dialog_reset),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.item_primary),
                    modifier = Modifier.clickable(onClick = { onFilterAction(CourseFilterAction.Reset) }),
                )
            }

            Spacer(modifier = modifier.height(20.dp))

            Column {
                Text(
                    text = stringResource(R.string.filter_dialog_difficulty_label),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.item_primary),
                )

                Spacer(modifier = modifier.height(20.dp))

                DifficultyButtons(
                    selectedDifficulties = coursesUiState.courseFilter.difficulties,
                    onDifficultyToggle = { difficulty: Difficulty ->
                        onFilterAction(CourseFilterAction.ToggleDifficulty(difficulty))
                    },
                )
            }

            Spacer(modifier = modifier.height(20.dp))

            Column(modifier = Modifier) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(R.string.filter_dialog_length_label),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.item_primary),
                    )
                    Text(
                        text = lengthRangeText(coursesUiState.courseFilter),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.item_primary),
                    )
                }

                Spacer(modifier = modifier.height(20.dp))

                RangeSlider(
                    value =
                        coursesUiState.courseFilter.lengthRangeAsFloat.start..coursesUiState.courseFilter.lengthRangeAsFloat.endInclusive,
                    onValueChange = { range: ClosedFloatingPointRange<Float> ->
                        onFilterAction(
                            CourseFilterAction.UpdateLengthRange(
                                range.start.toDouble(),
                                range.endInclusive.toDouble(),
                            ),
                        )
                    },
                    valueRange =
                        CourseFilter.MINIMUM_LENGTH_RANGE.toFloat()..CourseFilter.MAXIMUM_LENGTH_RANGE.toFloat(),
                    colors = sliderColors(),
                )
            }

            Spacer(modifier = modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                TextButton(
                    onClick = { onFilterAction(CourseFilterAction.Cancel) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 20.dp),
                ) {
                    Text(
                        text = stringResource(R.string.filter_dialog_cancel),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.item_primary),
                    )
                }

                FilterResultButton(
                    text =
                        stringResource(
                            R.string.filter_result_count,
                            coursesUiState.courses.size,
                        ),
                    enabled = coursesUiState.courses.isNotEmpty(),
                    onClick = { onFilterAction(CourseFilterAction.Apply) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun DifficultyButtons(
    selectedDifficulties: Set<Difficulty>,
    onDifficultyToggle: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        DifficultyButton(
            difficulty = Difficulty.EASY,
            text = stringResource(R.string.filter_dialog_difficulty_easy),
            onToggleDifficulty = onDifficultyToggle,
            selected = selectedDifficulties.contains(Difficulty.EASY),
            modifier = Modifier.weight(1f),
        )
        DifficultyButton(
            difficulty = Difficulty.NORMAL,
            text = stringResource(R.string.filter_dialog_difficulty_normal),
            onToggleDifficulty = onDifficultyToggle,
            selected = selectedDifficulties.contains(Difficulty.NORMAL),
            modifier = Modifier.weight(1f),
        )
        DifficultyButton(
            difficulty = Difficulty.HARD,
            text = stringResource(R.string.filter_dialog_difficulty_hard),
            onToggleDifficulty = onDifficultyToggle,
            selected = selectedDifficulties.contains(Difficulty.HARD),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun sliderColors() =
    SliderDefaults.colors(
        thumbColor = colorResource(R.color.point_secondary),
        activeTrackColor = colorResource(R.color.point_secondary),
        inactiveTrackColor = colorResource(R.color.item_tertiary),
        activeTickColor = colorResource(R.color.point_secondary),
        inactiveTickColor = colorResource(R.color.item_tertiary),
        disabledThumbColor = colorResource(R.color.item_tertiary),
        disabledActiveTrackColor = colorResource(R.color.item_tertiary),
        disabledActiveTickColor = colorResource(R.color.item_tertiary),
        disabledInactiveTrackColor = colorResource(R.color.item_tertiary),
        disabledInactiveTickColor = colorResource(R.color.item_tertiary),
    )

@Composable
private fun lengthRangeText(filter: CourseFilter): String {
    val start =
        filter.lengthRange.start.value
            .toInt()
    val end =
        filter.lengthRange.endInclusive.value
            .toInt()

    val min = CourseFilter.MINIMUM_LENGTH_RANGE.toInt()
    val max = CourseFilter.MAXIMUM_LENGTH_RANGE.toInt()

    return when {
        start == min && end != max -> stringResource(R.string.length_range_open_start, end)
        start != min && end == max -> stringResource(R.string.length_range_open_end, start)
        start != min && end != max -> stringResource(R.string.length_range, start, end)
        else -> stringResource(R.string.total_length_range)
    }
}

@PreviewLightDark
@Composable
private fun CourseFilterBottomSheetPreview() {
    CoursePickTheme {
        var showSheet by remember { mutableStateOf(true) }

        if (showSheet) {
            CourseFilterBottomSheet(
                coursesUiState = CoursesUiState(originalCourses = listOf()),
                onDismissRequest = { showSheet = false },
                onFilterAction = {},
            )
        }
    }
}
