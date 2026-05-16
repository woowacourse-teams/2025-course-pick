package io.coursepick.coursepick.presentation.customcourse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

@Composable
fun NetworkErrorView(
    onReconnect: () -> Unit,
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
            text = stringResource(R.string.network_connection_error_message),
            color = colorResource(R.color.item_primary),
            textAlign = TextAlign.Center,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier =
                Modifier
                    .clickable { onReconnect() }
                    .background(
                        color = colorResource(R.color.background_primary),
                        shape = RoundedCornerShape(8.dp),
                    ).border(
                        width = 1.dp,
                        color = colorResource(R.color.background_border),
                        shape = RoundedCornerShape(50),
                    ).padding(horizontal = 20.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "재시도",
                fontSize = 16.sp,
                color = colorResource(R.color.item_primary),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun NetworkErrorViewPreview() {
    NetworkErrorView(
        onReconnect = { },
        modifier = Modifier,
    )
}
