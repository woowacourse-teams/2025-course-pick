package io.coursepick.coursepick.presentation.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.model.Difficulty
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@Composable
fun DifficultyButton(
    difficulty: Difficulty,
    label: String,
    selectedDifficulties: Set<Difficulty>,
    onDifficultyToggle: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .toggleable(
                    value = selectedDifficulties.contains(difficulty),
                    role = Role.Switch,
                    onValueChange = { onDifficultyToggle(difficulty) },
                ).clip(RoundedCornerShape(size = 8.dp))
                .background(
                    if (selectedDifficulties.contains(difficulty)) {
                        colorResource(R.color.point_secondary)
                    } else {
                        colorResource(
                            R.color.gray2,
                        )
                    },
                ).padding(vertical = 20.dp)
                .padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorResource(R.color.background_primary),
        )
    }
}

@PreviewLightDark
@Composable
private fun DifficultyButtonPreview() {
    CoursePickTheme {
        DifficultyButton(
            label = "쉬움",
            difficulty = Difficulty.EASY,
            selectedDifficulties = setOf(),
            onDifficultyToggle = {},
        )
    }
}
