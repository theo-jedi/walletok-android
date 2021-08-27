package com.theost.walletok.utils

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.graphics.drawable.DrawableCompat

object ViewUtils {
    fun changeDrawableColor(drawable: Drawable, color: Int) {
        DrawableCompat.setTint(
            DrawableCompat.wrap(drawable),
            color
        )
    }

    fun showErrorMessage(view: View) {
        view.visibility = View.VISIBLE
        view.alpha = 0.0f
        view.animate().alpha(1.0f).duration = 500
    }

    fun hideErrorMessage(view: View) {
        view.alpha = 1.0f
        view.animate().alpha(0.0f).duration = 500
    }
}