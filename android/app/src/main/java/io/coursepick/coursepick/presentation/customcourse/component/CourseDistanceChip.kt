package io.coursepick.coursepick.presentation.customcourse.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.coursepick.coursepick.R
import io.coursepick.coursepick.domain.course.Distance
import io.coursepick.coursepick.presentation.formattedMeter

@Composable
fun CourseDistanceChip(
    distance: Distance,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .background(
                    color = colorResource(R.color.background_secondary),
                    shape = RoundedCornerShape(4.dp),
                ).border(
                    width = 1.dp,
                    color = colorResource(R.color.background_border),
                    shape = RoundedCornerShape(4.dp),
                ).padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text =
                stringResource(
                    R.string.course_item_distance_to_course_format,
                    formattedMeter(LocalContext.current, distance.meter),
                ),
            color = colorResource(R.color.point_primary),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@PreviewLightDark
@Composable
private fun CourseDistanceChipPreview() {
    CourseDistanceChip(Distance(5))
}
