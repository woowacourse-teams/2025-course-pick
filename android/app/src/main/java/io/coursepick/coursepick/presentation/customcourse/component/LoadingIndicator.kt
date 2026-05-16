package io.coursepick.coursepick.presentation.customcourse.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.coursepick.coursepick.R

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(
            modifier =
                modifier
                    .size(64.dp),
            color = colorResource(R.color.item_secondary),
            strokeWidth = 6.dp,
        )
    }
}

@PreviewLightDark
@Composable
private fun LoadingIndicatorPreview() {
    LoadingIndicator()
}
