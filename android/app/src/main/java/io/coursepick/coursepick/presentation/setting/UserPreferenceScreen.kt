package io.coursepick.coursepick.presentation.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

@Composable
fun UserPreferenceScreen(
    onOpenRouteFinderPreference: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.scrollable(rememberScrollState(), Orientation.Vertical)) {
        Column(
            Modifier
                .fillMaxWidth()
                .clickable { onOpenRouteFinderPreference() }
                .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.selected_route_finder_application_setting_title),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(10.dp))

            Text(text = stringResource(R.string.selected_route_finder_application_summary), fontSize = 16.sp)
        }
    }
}

@PreviewLightDark
@Composable
private fun UserPreferenceScreenPreview() {
    UserPreferenceScreen(
        onOpenRouteFinderPreference = { },
    )
}
