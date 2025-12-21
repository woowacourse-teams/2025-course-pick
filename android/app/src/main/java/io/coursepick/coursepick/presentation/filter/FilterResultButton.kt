package io.coursepick.coursepick.presentation.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    TextButton(
        onClick = onEnableChanged,
        enabled = isEnabled,
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
