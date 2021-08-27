package com.theost.walletok.presentation.base

import android.view.View
import com.theost.walletok.databinding.WidgetNetworkBinding

object ErrorMessageHelper {
    fun setUpErrorMessage(
        binding: WidgetNetworkBinding,
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