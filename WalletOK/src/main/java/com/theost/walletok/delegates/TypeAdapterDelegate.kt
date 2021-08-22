package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.AdapterDelegate
import com.theost.walletok.databinding.ItemListTypeBinding

class TypeAdapterDelegate(
    private val clickListener: (position: String) -> Unit
) : AdapterDelegate {

    private var lastSelected: ItemListTypeBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemListTypeBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as TypeItem)
    }

    override fun isOfViewType(item: Any): Boolean = item is TypeItem

    inner class ViewHolder(private val binding: ItemListTypeBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            if (binding != lastSelected) {
                if (lastSelected != null) lastSelected!!.typeCheck.visibility = View.INVISIBLE
                binding.typeCheck.visibility = View.VISIBLE
                lastSelected = binding

                clickListener(binding.typeTitle.text.toString())
            }
        }

        fun bind(type: TypeItem) {
            binding.typeTitle.text = type.name
            if (type.isSelected) {
                binding.typeCheck.visibility = View.VISIBLE
                lastSelected = binding
            }
        }

    }

}

data class TypeItem(
    val name: String,
    val isSelected: Boolean
)