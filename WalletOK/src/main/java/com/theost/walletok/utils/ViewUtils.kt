package com.theost.walletok.utils

import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.DrawableCompat

object ViewUtils {
    fun changeDrawableColor(drawable: Drawable, color: Int) {
        DrawableCompat.setTint(
            DrawableCompat.wrap(drawable),
            color
        )
    }
}