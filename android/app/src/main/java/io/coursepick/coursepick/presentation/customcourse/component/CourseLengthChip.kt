package io.coursepick.coursepick.presentation.customcourse.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Length
import io.coursepick.coursepick.presentation.formattedMeter

@Composable
fun CourseLengthChip(
    length: Length,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .height(28.dp)
                .background(
                    color = colorResource(R.color.background_secondary),
                    shape = RoundedCornerShape(4.dp),
                ).border(
                    width = 1.dp,
                    color = colorResource(R.color.background_border),
                    shape = RoundedCornerShape(4.dp),
                ).padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.icon_length),
            contentDescription = null,
            tint = Color.Unspecified,
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text =
                formattedMeter(
                    context = LocalContext.current,
                    meter = length.meter,
                ),
            color = colorResource(R.color.item_primary),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@PreviewLightDark
@Composable
private fun CourseLengthChipPreview() {
    CourseLengthChip(Length(5000))
}
