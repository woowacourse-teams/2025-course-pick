package io.coursepick.coursepick.presentation.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import androidx.core.graphics.scale

class BitmapScaler(
    private val context: Context,
) {
    fun scaleDrawable(
        @DrawableRes id: Int,
        factor: Double,
    ): Bitmap {
        val original: Bitmap = BitmapFactory.decodeResource(context.resources, id)
        return original.scale((original.width * factor).toInt(), (original.height * factor).toInt())
    }

    fun scaleDrawableToSize(
        @DrawableRes id: Int,
        widthPx: Float,
        heightPx: Float,
    ): Bitmap {
        val original: Bitmap = BitmapFactory.decodeResource(context.resources, id)
        return original.scale(widthPx.toInt(), heightPx.toInt())
    }

    fun scaleDrawableToWidth(
        @DrawableRes id: Int,
        widthPx: Float,
    ): Bitmap {
        val original: Bitmap = BitmapFactory.decodeResource(context.resources, id)
        return original.scale(widthPx.toInt(), (original.height / original.width.toFloat() * widthPx).toInt())
    }

    fun scaleDrawableToHeight(
        @DrawableRes id: Int,
        heightPx: Float,
    ): Bitmap {
        val original: Bitmap = BitmapFactory.decodeResource(context.resources, id)
        return original.scale((original.width / original.height.toFloat() * heightPx).toInt(), heightPx.toInt())
    }
}
