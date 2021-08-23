package com.theost.walletok.presentation.base

import android.view.View
import com.theost.walletok.databinding.WidgetErrorBinding

object ErrorMessageHelper {
    fun setUpErrorMessage(
        binding: WidgetErrorBinding,
        text: String? = null,
        iconResId: Int? = null,
        onRetry: () -> Unit
    ) {
        binding.errorLayout.visibility = View.VISIBLE
        text?.let { binding.errorTextView.text = it }
        iconResId?.let { binding.errorIcon.setImageResource(it) }
        binding.retryButton.setOnClickListener {
            binding.errorLayout.visibility = View.GONE
            onRetry.invoke()
        }
    }
}