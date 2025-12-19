package io.coursepick.coursepick.presentation.filter

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
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
    onDifficultyToggle: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = { onDifficultyToggle(difficulty) },
        shape = RoundedCornerShape(8.dp),
        modifier = modifier,
        colors =
            ButtonDefaults.textButtonColors(
                containerColor = colorResource(R.color.point_secondary),
                contentColor = colorResource(R.color.background_primary),
                disabledContainerColor = colorResource(R.color.gray2),
                disabledContentColor = colorResource(R.color.background_primary),
            ),
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 20.dp),
    ) {
        Text(
            text = label,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
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
            onDifficultyToggle = {},
        )
    }
}
