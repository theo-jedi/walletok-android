package com.theost.walletok.utils

import android.content.Context
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.theost.walletok.R

class ViewUtils {
    
    companion object {
        fun enableSubmitButton(context: Context, button: MaterialButton) {
            button.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_gray))
            button.setTextColor(ContextCompat.getColor(context, R.color.white))
            button.isEnabled = true
        }

        fun disableSubmitButton(context: Context, button: MaterialButton) {
            button.backgroundTintList =
                ColorStateList.valueOf(0)
            button.setTextColor(ContextCompat.getColor(context, R.color.black))
            button.isEnabled = false
        }
    }
    
}