package io.coursepick.coursepick.presentation.auth

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.search.ui.theme.CoursePickTheme

@Composable
fun AuthDialog(
    feature: AuthFeature,
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
                        .clip(CircleShape)
                        .clickable { onDismissRequest() }
                        .padding(12.dp),
                tint = colorResource(R.color.item_primary),
            )

            AuthContent(
                featureName = stringResource(feature.stringResourceId()),
                onKakaoLoginClick = onKakaoLoginClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@StringRes
private fun AuthFeature.stringResourceId(): Int =
    when (this) {
        is AuthFeature.ReportCourse -> R.string.report_course_feature_name
        is AuthFeature.DeleteReview -> R.string.delete_review_feature_name
        is AuthFeature.SubmitReview -> R.string.write_course_review_feature_name
        AuthFeature.CreateCustomCourse -> R.string.create_custom_course_feature_name
        AuthFeature.CustomCourse -> R.string.custom_course_feature_name
    }

@PreviewLightDark
@Composable
private fun AuthDialogPreview() {
    CoursePickTheme {
        AuthDialog(
            feature = AuthFeature.CreateCustomCourse,
            onDismissRequest = { },
            onKakaoLoginClick = { },
        )
    }
}
