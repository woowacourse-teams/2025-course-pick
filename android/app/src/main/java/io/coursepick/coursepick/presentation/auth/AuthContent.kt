package io.coursepick.coursepick.presentation.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@Composable
fun AuthContent(
    featureTitle: String,
    onKakaoLoginClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
                .background(color = colorResource(R.color.background_primary)),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.image_coursepick_mascot),
            contentDescription = null,
            modifier =
                Modifier
                    .size(48.dp)
                    .clip(CircleShape),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.auth_require, featureTitle),
            fontSize = 18.sp,
            color = colorResource(R.color.item_primary),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.image_kakao_login),
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .clickable { onKakaoLoginClick() },
            contentScale = ContentScale.FillWidth,
        )
    }
}

@PreviewLightDark
@Composable
private fun AuthContentPreview() {
    CoursePickTheme {
        AuthContent(
            featureTitle = "즐겨찾기",
            onKakaoLoginClick = {},
        )
    }
}
