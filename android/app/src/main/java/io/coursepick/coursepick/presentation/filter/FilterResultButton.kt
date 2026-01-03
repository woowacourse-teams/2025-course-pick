package io.coursepick.coursepick.presentation.filter

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TextButton(
        onClick = onClick,
        enabled = enabled,
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
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@PreviewLightDark
@Composable
private fun FilterResultButtonPreview() {
    CoursePickTheme {
        var enabled by remember { mutableStateOf(true) }
        FilterResultButton(
            text = stringResource(R.string.filter_result_count, 10),
            enabled = enabled,
            onClick = { enabled != enabled },
        )
    }
}
