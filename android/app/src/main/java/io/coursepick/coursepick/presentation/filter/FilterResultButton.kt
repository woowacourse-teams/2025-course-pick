package io.coursepick.coursepick.presentation.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@Composable
fun FilterResultButton(
    label: String,
    isActive: Boolean,
    onActiveChanged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clickable(
                    enabled = isActive,
                    onClick = onActiveChanged,
                ).clip(RoundedCornerShape(size = 8.dp))
                .background(
                    if (isActive) {
                        colorResource(R.color.point_secondary)
                    } else {
                        colorResource(
                            R.color.gray2,
                        )
                    },
                ).padding(vertical = 20.dp, horizontal = 4.dp),
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
private fun FilterResultButtonPreview() {
    CoursePickTheme {
        var isActive by remember { mutableStateOf(true) }
        FilterResultButton(
            label = "쉬움",
            isActive = isActive,
            onActiveChanged = { isActive = !isActive },
        )
    }
}
