package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.AdapterDelegate
import com.theost.walletok.databinding.ItemListCategoryBinding

class CategoryAdapterDelegate(
    private val clickListener: (position: Int) -> Unit
) : AdapterDelegate {

    private var lastSelected: ItemListCategoryBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemListCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as CategoryItem)
    }

    override fun isOfViewType(item: Any): Boolean = item is CategoryItem

    inner class ViewHolder(private val binding: ItemListCategoryBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (binding != lastSelected) {
                if (lastSelected != null) lastSelected!!.categoryCheck.visibility = View.INVISIBLE
                binding.categoryCheck.visibility = View.VISIBLE
                lastSelected = binding

                clickListener(adapterPosition)
            }
        }

        fun bind(category: CategoryItem) {
            binding.categoryTitle.text = category.name
            binding.categoryIcon.setImageResource(category.icon)
            if (category.isSelected) {
                binding.categoryCheck.visibility = View.VISIBLE
                lastSelected = binding
            }
        }

    }

}

data class CategoryItem(
    val id: Int,
    val name: String,
    val icon: Int,
    val isSelected: Boolean
)