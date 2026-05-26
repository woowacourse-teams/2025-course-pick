package io.coursepick.coursepick.presentation.customcourse.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.compat.OnReconnectListener

@Composable
fun NetworkErrorContent(
    onReconnect: OnReconnectListener,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .background(colorResource(R.color.background_primary))
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.failure_no_network_message),
            color = colorResource(R.color.item_primary),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier =
                Modifier
                    .border(
                        width = 1.dp,
                        color = colorResource(R.color.background_border),
                        shape = RoundedCornerShape(50),
                    ).clip(RoundedCornerShape(50))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onReconnect::onReconnect,
                    ).padding(horizontal = 20.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.failure_retry_button),
                fontSize = 16.sp,
                color = colorResource(R.color.item_primary),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun NetworkErrorContentPreview() {
    NetworkErrorContent(
        onReconnect = { },
        modifier = Modifier,
    )
}
