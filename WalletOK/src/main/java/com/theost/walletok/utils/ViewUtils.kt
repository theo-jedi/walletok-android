package com.theost.walletok.utils

import android.view.View

object ViewUtils {

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