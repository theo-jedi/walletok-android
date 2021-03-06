package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.databinding.ItemListButtonBinding
import com.theost.walletok.presentation.base.AdapterDelegate
import com.theost.walletok.presentation.base.DelegateItem

class ButtonAdapterDelegate(
    private val clickListener: (buttonType: ListButtonType) -> Unit
) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemListButtonBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListButton)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListButton

    class ViewHolder(
        private val binding: ItemListButtonBinding,
        private val clickListener: (buttonType: ListButtonType) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(button: ListButton) {
            binding.root.setOnClickListener { clickListener(button.type) }
            binding.listButton.text = button.text
            binding.listButton.isEnabled = button.isEnabled
            binding.listButton.visibility = if (button.isVisible) View.VISIBLE else View.INVISIBLE
        }

    }

}

data class ListButton(
    val text: String,
    val type: ListButtonType,
    val isVisible: Boolean,
    val isEnabled: Boolean
) : DelegateItem {
    override fun content(): Any = text

    override fun id(): Any = type
}

enum class ListButtonType {
    CREATION, DELETION
}