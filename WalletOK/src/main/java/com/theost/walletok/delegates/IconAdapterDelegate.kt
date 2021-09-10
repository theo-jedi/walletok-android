package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.theost.walletok.App
import com.theost.walletok.databinding.ItemListIconBinding
import com.theost.walletok.presentation.base.AdapterDelegate

class IconAdapterDelegate(private val clickListener: (iconUrl: Int) -> Unit) :
    AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemListIconBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListIcon)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListIcon

    class ViewHolder(
        private val binding: ItemListIconBinding,
        private val clickListener: (iconUrl: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(iconItem: ListIcon) {
            binding.root.alpha = 0.5f
            binding.root.isEnabled = true
            binding.root.setOnClickListener { clickListener(iconItem.iconUrl) }
            binding.cateogryIcon.load(iconItem.iconUrl, App.svgImageLoader)
            binding.categoryBackground.setColorFilter(iconItem.iconColor, android.graphics.PorterDuff.Mode.SRC_IN)
            if (iconItem.isSelected) {
                binding.root.alpha = 1.0f
                binding.root.isEnabled = false
            }
        }

    }

}

data class ListIcon(
    val iconUrl: Int,
    val iconColor: Int,
    val isSelected: Boolean
)