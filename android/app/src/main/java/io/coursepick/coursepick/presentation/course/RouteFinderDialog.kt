package io.coursepick.coursepick.presentation.course

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import io.coursepick.coursepick.domain.preferences.RouteFinder

@Composable
fun RouteFinderDialog(
    onConfirm: (routeFinder: RouteFinder, rememberChoice: Boolean) -> Unit,
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
                    .background(colorResource(R.color.background_primary))
                    .padding(16.dp),
        ) {
            Text(
                text = stringResource(R.string.selected_route_finder_application_dialog_title),
                color = colorResource(R.color.item_primary),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.selected_route_finder_application_dialog_description),
                color = colorResource(R.color.item_primary),
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(16.dp))

            var rememberChoice by rememberSaveable { mutableStateOf(false) }

            RouteFinderOptions(
                onSelectOption = { option: RouteFinderApplication -> onConfirm(option.routeFinder, rememberChoice) },
                modifier = Modifier.fillMaxWidth(),
            )

            RememberChoiceCheckbox(
                checked = rememberChoice,
                onCheckedChange = { checked: Boolean -> rememberChoice = checked },
                modifier = Modifier.align(Alignment.Start),
            )
        }
    }
}

@Composable
private fun RouteFinderOptions(
    onSelectOption: (RouteFinderApplication) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.background_secondary)),
    ) {
        RouteFinderApplication.Entries.forEachIndexed { index: Int, routeFinder: RouteFinderApplication ->
            if (routeFinder != RouteFinderApplication.None) {
                Text(
                    text = stringResource(routeFinder.nameId),
                    color = colorResource(R.color.item_primary),
                    fontSize = 16.sp,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .fillMaxWidth()
                            .clickable { onSelectOption(routeFinder) }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                )

                if (index != RouteFinderApplication.Entries.lastIndex) {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = colorResource(R.color.background_border),
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun RememberChoiceCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = colorResource(R.color.point_primary)),
        )

        Text(
            text = stringResource(R.string.selected_route_finder_application_dialog_set_default),
            color = colorResource(R.color.item_primary),
            fontSize = 16.sp,
            modifier =
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCheckedChange(!checked) }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
        )
    }
}

@PreviewLightDark
@Composable
private fun RouteFinderDialogPreview() {
    RouteFinderDialog(
        onConfirm = { _, _ -> },
        onDismiss = { },
    )
}
