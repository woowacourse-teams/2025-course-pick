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
import androidx.compose.material3.SliderColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    onReset: () -> Unit,
    onEasy: () -> Unit,
    onNormar: () -> Unit,
    onHard: () -> Unit,
    onRangeSliderValueChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onCancel: () -> Unit,
    onResult: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 10.dp),
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
                modifier = Modifier.clickable(onClick = onReset),
            )
        }
        Text(
            text = stringResource(R.string.filter_dialog_difficulty_label),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 20.dp),
            color = colorResource(R.color.item_primary),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            RoundedCornerToggleButton(
                label = stringResource(R.string.filter_dialog_difficulty_easy),
                isActive = coursesUiState.courseFilter.difficulties.contains(Difficulty.EASY),
                onActivedChanged = onEasy,
                modifier = Modifier.weight(1f),
            )
            RoundedCornerToggleButton(
                label = stringResource(R.string.filter_dialog_difficulty_normal),
                isActive = coursesUiState.courseFilter.difficulties.contains(Difficulty.NORMAL),
                onActivedChanged = onNormar,
                modifier = Modifier.weight(1f),
            )
            RoundedCornerToggleButton(
                label = stringResource(R.string.filter_dialog_difficulty_hard),
                isActive = coursesUiState.courseFilter.difficulties.contains(Difficulty.HARD),
                onActivedChanged = onHard,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(R.string.filter_dialog_length_label),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 20.dp),
                color = colorResource(R.color.item_primary),
            )
            Text(
                text = lengthRangeText(coursesUiState.courseFilter),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 20.dp),
            )
        }
        RangeSlider(
            value =
                coursesUiState.courseFilter.lengthRange.start.value
                    .toFloat()..coursesUiState.courseFilter.lengthRange.endInclusive.value
                    .toFloat(),
            onValueChange = onRangeSliderValueChange,
            valueRange = 0f..21f,
            steps = 0,
            colors =
                SliderColors(
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
                ),
            modifier = Modifier.padding(bottom = 20.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .clickable(onClick = onCancel)
                        .clip(RoundedCornerShape(size = 8.dp))
                        .padding(vertical = 20.dp)
                        .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center,
                content = {
                    Text(
                        text = stringResource(R.string.filter_dialog_cancel),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.item_primary),
                    )
                },
            )

            RoundedCornerToggleButton(
                label = stringResource(R.string.filter_result_count, coursesUiState.courses.size),
                isActive = coursesUiState.courses.isNotEmpty(),
                onActivedChanged = onResult,
                modifier = Modifier.weight(1f),
                enabled = coursesUiState.courses.isNotEmpty(),
            )
        }
    }
}

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

@Preview(showBackground = true)
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
                            Kilometer(0.0)..Kilometer(10.0),
                        ),
                ),
            onRangeSliderValueChange = { 0f..10f },
            onCancel = {},
            onReset = { },
            onEasy = {},
            onNormar = { },
            onHard = {},
            onResult = {},
        )
    }
}
