package io.coursepick.coursepick.presentation.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@Composable
fun FilterResultButton(
    label: String,
    isEnabled: Boolean,
    onEnableChanged: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clickable(
                    enabled = isEnabled,
                    onClick = onEnableChanged,
                ).clip(RoundedCornerShape(size = 8.dp))
                .background(
                    if (isEnabled) {
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
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            FilterResultButton(
                label = stringResource(R.string.filter_result_count, 10),
                isEnabled = true,
                onEnableChanged = { },
            )

            FilterResultButton(
                label = stringResource(R.string.filter_result_count, 0),
                isEnabled = false,
                onEnableChanged = { },
            )
        }
    }
}
