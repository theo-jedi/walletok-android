package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.databinding.ItemListIconBinding
import com.theost.walletok.presentation.base.AdapterDelegate

class IconAdapterDelegate(private val clickListener: (iconRes: Int) -> Unit) :
    AdapterDelegate {

    private var backgroundColor: Int = 0
    private var iconRes: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemListIconBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListIcon, backgroundColor, iconRes)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListIcon

    fun setBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
    }

    fun setSelectedIcon(iconRes: Int) {
        this.iconRes = iconRes
    }

    class ViewHolder(
        private val binding: ItemListIconBinding,
        private val clickListener: (iconRes: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(iconItem: ListIcon, backgroundColor: Int, iconRes: Int) {
            binding.root.alpha = 1.0f
            binding.root.isEnabled = true
            binding.root.setOnClickListener { clickListener(iconItem.iconRes) }
            binding.cateogryIcon.setImageResource(iconItem.iconRes)
            binding.categoryBackground.setColorFilter(backgroundColor, android.graphics.PorterDuff.Mode.SRC_IN)
            if (iconItem.iconRes == iconRes) {
                binding.root.alpha = 0.5f
                binding.root.isEnabled = false
            }
        }

    }

}

data class ListIcon(
    val iconRes: Int
)