package io.coursepick.coursepick.presentation.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R
import io.coursepick.coursepick.data.preferences.RouteFinder
import io.coursepick.coursepick.presentation.course.RouteFinderUiModel

@Composable
fun RouteFinderPreferenceDialog(
    selection: RouteFinder?,
    onConfirm: (RouteFinder?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = colorResource(R.color.background_primary))
                    .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.route_finder_preferences_dialog_title),
                color = colorResource(R.color.item_primary),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.route_finder_preferences_dialog_summary),
                color = colorResource(R.color.item_primary),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            var selectedOption: RouteFinder? by remember(selection) { mutableStateOf(selection) }

            RouteFinderPreferenceOptions(
                options = listOf(null) + RouteFinderUiModel.Entries,
                selection = selectedOption,
                onSelectOption = { option: RouteFinder? -> selectedOption = option },
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(16.dp))

            RouteFinderPreferenceDialogButtons(
                onCancel = onDismiss,
                onConfirm = { onConfirm(selectedOption) },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun RouteFinderPreferenceOptions(
    options: List<RouteFinderUiModel?>,
    selection: RouteFinder?,
    onSelectOption: (RouteFinder?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.background_secondary)),
    ) {
        options.forEachIndexed { index: Int, routeFinder: RouteFinderUiModel? ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .fillMaxWidth()
                        .clickable { onSelectOption(routeFinder?.routeFinder) }
                        .padding(horizontal = 12.dp),
            ) {
                RadioButton(
                    selected = routeFinder?.routeFinder == selection,
                    onClick = { onSelectOption(routeFinder?.routeFinder) },
                    colors = RadioButtonDefaults.colors(selectedColor = colorResource(R.color.point_primary)),
                )

                Text(
                    text = stringResource(routeFinder?.nameId ?: R.string.route_finder_preferences_dialog_entry_none),
                    color = colorResource(R.color.item_primary),
                    fontSize = 16.sp,
                )
            }

            if (index != options.lastIndex) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = colorResource(R.color.background_border),
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun RouteFinderPreferenceDialogButtons(
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .weight(1F)
                    .clip(RoundedCornerShape(50))
                    .clickable { onCancel() }
                    .background(colorResource(R.color.background_tertiary))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.route_finder_preferences_dialog_cancel_button),
                fontSize = 16.sp,
                color = colorResource(R.color.item_tertiary),
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .weight(1F)
                    .clip(RoundedCornerShape(50))
                    .clickable { onConfirm() }
                    .background(colorResource(R.color.point_primary))
                    .padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            Text(
                text = stringResource(R.string.route_finder_preferences_dialog_confirm_button),
                color = colorResource(R.color.item_primary),
                fontSize = 16.sp,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun RouteFinderPreferenceDialogPreview() {
    RouteFinderPreferenceDialog(
        selection = null,
        onConfirm = { },
        onDismiss = { },
    )
}
