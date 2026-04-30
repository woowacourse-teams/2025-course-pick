package io.coursepick.coursepick.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R
import io.coursepick.coursepick.data.preference.RouteFinder
import io.coursepick.coursepick.presentation.course.RouteFinderUiModel

@Composable
fun RouteFinderPreferenceDialog(
    onConfirm: (RouteFinder?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismiss) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = colorResource(R.color.background_primary))
                    .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.selected_route_finder_application_setting_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )

            Text(text = stringResource(R.string.selected_route_finder_application_dialog_description), fontSize = 18.sp)

            LazyColumn {
                item {
                    Text(
                        text = stringResource(R.string.selected_route_finder_application_entry_none_map),
                        modifier = Modifier.clickable { onConfirm(null) },
                    )
                }

                items(RouteFinderUiModel.Entries) { routeFinder: RouteFinderUiModel ->
                    Text(
                        text = stringResource(routeFinder.nameId),
                        modifier = Modifier.clickable { onConfirm(routeFinder.routeFinder) },
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun RouteFinderPreferenceDialogPreview() {
    RouteFinderPreferenceDialog(
        onConfirm = { },
        onDismiss = { },
    )
}
