package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.AdapterDelegate
import com.theost.walletok.databinding.ItemListTypeBinding

class TypeAdapterDelegate(
    private val clickListener: (position: Int) -> Unit
) : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemListTypeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as TypeItem)
    }

    override fun isOfViewType(item: Any): Boolean = item is TypeItem

    class ViewHolder(private val binding: ItemListTypeBinding, private val clickListener: (position: Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(type: TypeItem) {
            binding.root.setOnClickListener { clickListener(adapterPosition) }
            binding.typeTitle.text = type.name
            binding.typeCheck.visibility = View.INVISIBLE
            if (type.isSelected) { binding.typeCheck.visibility = View.VISIBLE }
        }

    }

}

data class TypeItem(
    val name: String,
    var isSelected: Boolean
)