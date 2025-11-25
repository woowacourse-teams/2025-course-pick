package io.coursepick.coursepick.presentation.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

@Composable
fun SettingsScreen(
    onNavigateToPreferences: () -> Unit,
    onNavigateToFeedback: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToOpenSourceNotice: () -> Unit,
    onShowVerifiedLocations: () -> Unit,
    installationId: String,
    onCopyInstallationId: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(colorResource(R.color.background_primary))
                .padding(20.dp)
                .statusBarsPadding(),
    ) {
        Column(
            modifier =
                Modifier
                    .weight(1F)
                    .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = "설정",
                fontSize = 16.sp,
                color = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .clickable { onNavigateToPreferences() }
                        .fillMaxWidth()
                        .height(50.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(horizontal = 10.dp),
            )

            Text(
                text = "사용자 피드백 창구",
                fontSize = 16.sp,
                color = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .clickable { onNavigateToFeedback() }
                        .fillMaxWidth()
                        .height(50.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(horizontal = 10.dp),
            )

            Text(
                text = "개인정보처리방침",
                fontSize = 16.sp,
                color = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .clickable { onNavigateToPrivacyPolicy() }
                        .fillMaxWidth()
                        .height(50.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(horizontal = 10.dp),
            )

            Text(
                text = "오픈소스 라이선스 고지",
                fontSize = 16.sp,
                color = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .clickable { onNavigateToOpenSourceNotice() }
                        .fillMaxWidth()
                        .height(50.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(horizontal = 10.dp),
            )

            Text(
                text = "코스가 검증된 지역 보기",
                fontSize = 16.sp,
                color = colorResource(R.color.item_primary),
                modifier =
                    Modifier
                        .clickable { onShowVerifiedLocations() }
                        .fillMaxWidth()
                        .height(50.dp)
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(horizontal = 10.dp),
            )
        }

        Text(
            text = "사용자 ID: $installationId",
            fontSize = 14.sp,
            color = colorResource(R.color.item_primary),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clickable { onCopyInstallationId() },
        )
    }
}

@PreviewLightDark
@Composable
fun Preview() {
    SettingsScreen(
        onNavigateToPreferences = {},
        onNavigateToFeedback = {},
        onNavigateToPrivacyPolicy = {},
        onNavigateToOpenSourceNotice = {},
        onShowVerifiedLocations = {},
        installationId = "Installation ID preview",
        onCopyInstallationId = {},
    )
}
