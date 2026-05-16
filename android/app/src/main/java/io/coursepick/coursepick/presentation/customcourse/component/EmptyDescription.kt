package io.coursepick.coursepick.presentation.customcourse.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

@Composable
fun EmptyDescription(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(
            text = text,
            fontSize = 20.sp,
            modifier =
                Modifier
                    .fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = colorResource(R.color.item_primary),
        )
    }
}

@PreviewLightDark
@Composable
private fun EmptyDescriptionPreview() {
    EmptyDescription("등록한 코스가 없어요")
}
