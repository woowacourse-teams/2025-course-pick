package io.coursepick.coursepick.presentation.coursedetail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.presentation.toDistanceText

@Composable
fun CourseLengthInfo(
    length: Double,
    modifier: Modifier = Modifier,
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Icon(
            painter = painterResource(R.drawable.icon_length),
            contentDescription = null,
            tint = colorResource(R.color.item_primary),
        )

        Spacer(Modifier.width(10.dp))

        Text(
            text = length.toDistanceText(),
            color = colorResource(R.color.item_primary),
            fontSize = 18.sp,
        )
    }
}
