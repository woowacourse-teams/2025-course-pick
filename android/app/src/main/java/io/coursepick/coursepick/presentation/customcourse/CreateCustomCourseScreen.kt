package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.coursepick.coursepick.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCustomCourseScreen(modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = { Text(text = "코스 추가") },
        navigationIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.padding(start = 10.dp),
            )
        },
        actions = {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = null,
                modifier = Modifier.padding(end = 10.dp),
            )
        },
        colors =
            TopAppBarColors(
                containerColor = colorResource(R.color.background_primary),
                scrolledContainerColor = colorResource(R.color.background_primary),
                navigationIconContentColor = colorResource(R.color.item_primary),
                titleContentColor = colorResource(R.color.item_primary),
                actionIconContentColor = colorResource(R.color.item_primary),
            ),
    )
}

@PreviewLightDark
@Composable
fun CreateCustomCourseScreenPreview() {
    CreateCustomCourseScreen()
}
