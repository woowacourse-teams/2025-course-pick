package io.coursepick.coursepick.presentation.coursedetail

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.coursepick.coursepick.R

@Composable
fun StarRating(
    rating: Float,
    starSize: Dp,
    modifier: Modifier = Modifier,
    onSelectRating: ((Float) -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier,
    ) {
        val animatedRating by animateFloatAsState(targetValue = rating, animationSpec = tween())

        List(5) { index: Int ->
            Box(
                Modifier
                    .size(starSize)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = onSelectRating != null,
                    ) { onSelectRating?.invoke((index + 1).toFloat()) },
            ) {
                Icon(
                    painter = painterResource(R.drawable.icon_star),
                    contentDescription = null,
                    tint = colorResource(R.color.item_tertiary),
                    modifier = Modifier.fillMaxSize(),
                )

                Icon(
                    painter = painterResource(R.drawable.icon_star),
                    contentDescription = null,
                    tint = colorResource(R.color.point_primary),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                clipRect(right = this.size.width * (animatedRating - index)) {
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
private fun StarRatingPreview_0() {
    StarRating(rating = 0F, starSize = 18.dp)
}

@PreviewLightDark
@Composable
private fun StarRatingPreview_3_5() {
    StarRating(rating = 3.5F, starSize = 18.dp)
}

@PreviewLightDark
@Composable
private fun StarRatingPreview_5() {
    StarRating(rating = 5F, starSize = 18.dp)
}
