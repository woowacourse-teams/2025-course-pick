package io.coursepick.coursepick.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@Composable
fun AuthDialog(
    featureName: String,
    onDismissRequest: () -> Unit,
    onKakaoLoginClick: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(color = colorResource(R.color.background_primary))
                    .padding(16.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier =
                    Modifier
                        .size(48.dp)
                        .clickable { onDismissRequest() }
                        .padding(12.dp),
                tint = colorResource(R.color.item_primary),
            )

            AuthContent(
                featureName = featureName,
                onKakaoLoginClick = onKakaoLoginClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AuthDialogPreview() {
    CoursePickTheme {
        AuthDialog(
            featureName = "즐겨찾기",
            onDismissRequest = { },
            onKakaoLoginClick = { },
        )
    }
}
