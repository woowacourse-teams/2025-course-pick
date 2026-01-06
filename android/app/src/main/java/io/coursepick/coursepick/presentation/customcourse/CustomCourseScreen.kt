package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

@Composable
fun CustomCourseScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = "나의 코스",
            fontSize = 24.sp,
            color = colorResource(R.color.item_primary),
        )
    }
}

@PreviewLightDark
@Composable
fun CustomCourseScreenPreview() {
    CustomCourseScreen()
}
