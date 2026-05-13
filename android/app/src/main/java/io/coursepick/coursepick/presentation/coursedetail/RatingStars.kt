package io.coursepick.coursepick.presentation.coursedetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.coursepick.coursepick.R

@Composable
fun RatingStars(
    rating: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        List(5) { index: Int ->
            Box {
                Icon(
                    painter = painterResource(R.drawable.icon_star),
                    contentDescription = null,
                    tint = colorResource(R.color.item_tertiary),
                )

                Icon(
                    painter = painterResource(R.drawable.icon_star),
                    contentDescription = null,
                    tint = colorResource(R.color.point_primary),
                    modifier =
                        Modifier.drawWithContent {
                            clipRect(right = size.width * (rating - index)) {
                                this@drawWithContent.drawContent()
                            }
                        },
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun RatingStarsPreview_0() {
    RatingStars(rating = 0F)
}

@PreviewLightDark
@Composable
private fun RatingStarsPreview_3_5() {
    RatingStars(rating = 3.5F)
}

@PreviewLightDark
@Composable
private fun RatingStarsPreview_5() {
    RatingStars(rating = 5F)
}
