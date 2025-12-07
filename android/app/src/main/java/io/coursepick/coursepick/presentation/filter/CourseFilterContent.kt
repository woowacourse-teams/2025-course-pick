package io.coursepick.coursepick.presentation.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Kilometer
import io.coursepick.coursepick.presentation.course.CoursesUiState
import io.coursepick.coursepick.presentation.model.Difficulty
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@Composable
fun CourseFilterContent(
    coursesUiState: CoursesUiState,
    onFilterAction: (CourseFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(start = 20.dp, end = 20.dp, bottom = 10.dp),
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

        Column {
            Text(
                text = stringResource(R.string.filter_dialog_difficulty_label),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 20.dp),
                color = colorResource(R.color.item_primary),
            )

            DifficultyButtons(
                selectedDifficulties = coursesUiState.courseFilter.difficulties,
                onDifficultyToggle = { difficulty: Difficulty ->
                    onFilterAction(CourseFilterAction.ToggleDifficulty(difficulty))
                },
            )
        }

        Column(modifier = modifier) {
            LengthRangeHeader(filter = coursesUiState.courseFilter)

            LengthRangeSlider(
                currentRange = coursesUiState.courseFilter.lengthRange,
                onRangeChange = { start: Double, end: Double ->
                    onFilterAction(CourseFilterAction.UpdateLengthRange(start, end))
                },
            )
        }

        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier =
                    modifier
                        .clickable(onClick = { onFilterAction(CourseFilterAction.Cancel) })
                        .clip(RoundedCornerShape(size = 8.dp))
                        .padding(vertical = 20.dp)
                        .padding(horizontal = 4.dp)
                        .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.filter_dialog_cancel),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.item_primary),
                )
            }

            FilterResultButton(
                label = stringResource(R.string.filter_result_count, coursesUiState.courses.size),
                isEnabled = coursesUiState.courses.isNotEmpty(),
                onActiveChanged = { onFilterAction(CourseFilterAction.Apply) },
                modifier = Modifier.weight(1f),
            )
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
            label = stringResource(R.string.filter_dialog_difficulty_easy),
            selectedDifficulties = selectedDifficulties,
            onDifficultyToggle = onDifficultyToggle,
            modifier = Modifier.weight(1f),
        )
        DifficultyButton(
            difficulty = Difficulty.NORMAL,
            label = stringResource(R.string.filter_dialog_difficulty_normal),
            selectedDifficulties = selectedDifficulties,
            onDifficultyToggle = onDifficultyToggle,
            modifier = Modifier.weight(1f),
        )
        DifficultyButton(
            difficulty = Difficulty.HARD,
            label = stringResource(R.string.filter_dialog_difficulty_hard),
            selectedDifficulties = selectedDifficulties,
            onDifficultyToggle = onDifficultyToggle,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun LengthRangeHeader(
    filter: CourseFilter,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = stringResource(R.string.filter_dialog_length_label),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 20.dp),
            color = colorResource(R.color.item_primary),
        )
        Text(
            text = lengthRangeText(filter),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 20.dp),
            color = colorResource(R.color.item_primary),
        )
    }
}

@Composable
private fun LengthRangeSlider(
    currentRange: ClosedRange<Kilometer>,
    onRangeChange: (Double, Double) -> Unit,
    modifier: Modifier = Modifier,
) {
    val start = currentRange.start.value.toFloat()
    val end = currentRange.endInclusive.value.toFloat()

    RangeSlider(
        value = start..end,
        onValueChange = { range ->
            onRangeChange(range.start.toDouble(), range.endInclusive.toDouble())
        },
        valueRange = 0f..21f,
        steps = 0,
        colors = sliderColors(),
        modifier = modifier.padding(bottom = 20.dp),
    )
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
private fun CourseFilterContentPreview() {
    CoursePickTheme {
        CourseFilterContent(
            coursesUiState =
                CoursesUiState(
                    originalCourses = listOf(),
                    query = "",
                    courseFilter =
                        CourseFilter.None.copy(
                            lengthRange = Kilometer(0.0)..Kilometer(10.0),
                        ),
                ),
            onFilterAction = {},
        )
    }
}
