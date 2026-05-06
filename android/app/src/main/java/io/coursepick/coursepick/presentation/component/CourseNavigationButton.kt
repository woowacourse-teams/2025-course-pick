package io.coursepick.coursepick.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R

@Composable
fun CourseNavigationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(9999.dp)

    Row(
        modifier =
            modifier
                .height(36.dp)
                .background(
                    color = colorResource(R.color.background_secondary),
                    shape = shape,
                ).border(
                    width = 1.dp,
                    color = colorResource(R.color.background_border),
                    shape = shape,
                ).clickable(onClick = onClick)
                .padding(
                    start = 2.dp,
                    end = 8.dp,
                    top = 6.dp,
                    bottom = 6.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_navigate),
            contentDescription = null,
            tint = Color.Unspecified,
        )

        Text(
            text = stringResource(R.string.course_item_navigate_to_course_button),
            color = colorResource(R.color.item_primary),
            fontSize = 12.sp,
        )
    }
}

@PreviewLightDark
@Composable
fun CourseNavigationButtonPreview() {
    CourseNavigationButton(onClick = {})
}
