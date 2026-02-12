package io.coursepick.coursepick.presentation.search.ui.theme

import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import io.coursepick.coursepick.R

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

@Composable
fun sliderColors() =
    SliderDefaults.colors(
        thumbColor = colorResource(R.color.point_secondary),
        activeTrackColor = colorResource(R.color.point_secondary),
        inactiveTrackColor = colorResource(R.color.item_tertiary),
        activeTickColor = colorResource(R.color.point_primary),
        inactiveTickColor = colorResource(R.color.item_secondary),
        disabledThumbColor = colorResource(R.color.item_tertiary),
        disabledActiveTrackColor = colorResource(R.color.item_tertiary),
        disabledActiveTickColor = colorResource(R.color.item_tertiary),
        disabledInactiveTrackColor = colorResource(R.color.item_tertiary),
        disabledInactiveTickColor = colorResource(R.color.item_tertiary),
    )
