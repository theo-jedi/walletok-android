package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.databinding.ItemListIconBinding
import com.theost.walletok.presentation.base.AdapterDelegate

class IconAdapterDelegate(private var backgroundColor: Int, private val clickListener: (position: Int, iconRes: Int) -> Unit) :
    AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemListIconBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListIcon, backgroundColor)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListIcon

    fun setBackgroundColor(backgroundColor: Int) {
        this.backgroundColor = backgroundColor
    }

    class ViewHolder(private val binding: ItemListIconBinding, private val clickListener: (position: Int, iconRes: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(iconItem: ListIcon, backgroundColor: Int) {
            binding.root.alpha = 1.0f
            binding.root.setOnClickListener { clickListener(adapterPosition, iconItem.iconRes) }
            binding.cateogryIcon.setImageResource(iconItem.iconRes)
            binding.categoryBackground.setColorFilter(backgroundColor, android.graphics.PorterDuff.Mode.SRC_IN)
            if (iconItem.isSelected) binding.root.alpha = 0.5f
        }

    }

}

data class ListIcon(
    val iconRes: Int,
    var isSelected: Boolean
)