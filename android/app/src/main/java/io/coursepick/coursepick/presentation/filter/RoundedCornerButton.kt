package io.coursepick.coursepick.presentation.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun RoundedCornerButton(
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                )
                .clip(RoundedCornerShape(size = 8.dp))
                .background(colorResource(R.color.point_secondary))
                .padding(vertical = 20.dp).padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
        content = {
            Text(
                text = label,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colorResource(R.color.background_primary),
            )
        },
    )
}

@PreviewLightDark
@Composable
private fun RoundedCornerButton() {
    CoursePickTheme {
        RoundedCornerButton(
            label = "쉬움",
            enabled = true,
            onClick = { },
        )
    }
}
