package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.theost.walletok.databinding.ItemListCategoryBinding
import com.theost.walletok.presentation.base.AdapterDelegate
import com.theost.walletok.utils.ViewUtils

class CategoryAdapterDelegate(
    private val clickListener: (position: Int) -> Unit
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemListCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as CategoryItem)
    }

    override fun isOfViewType(item: Any): Boolean = item is CategoryItem

    class ViewHolder(
        private val binding: ItemListCategoryBinding,
        private val clickListener: (position: Int) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryItem) {
            binding.root.setOnClickListener { clickListener(adapterPosition) }
            binding.categoryTitle.text = category.name
            binding.categoryIcon.load(category.iconUrl)
            ViewUtils.changeDrawableColor(binding.categoryCircle.background, category.iconColor)
            binding.categoryCheck.visibility = View.INVISIBLE
            if (category.isSelected) {
                binding.categoryCheck.visibility = View.VISIBLE
            }
        }

    }

}

data class CategoryItem(
    val id: Int,
    val name: String,
    val iconUrl: String,
    val iconColor: Int,
    var isSelected: Boolean
)