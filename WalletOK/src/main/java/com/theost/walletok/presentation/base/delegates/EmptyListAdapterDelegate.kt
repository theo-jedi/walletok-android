package com.theost.walletok.presentation.base.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.databinding.ItemEmptyListBinding
import com.theost.walletok.presentation.base.AdapterDelegate

class EmptyListAdapterDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemEmptyListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as EmptyListContent)
    }

    override fun isOfViewType(item: Any) = item is EmptyListContent

    class ViewHolder(val binding: ItemEmptyListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: EmptyListContent) {
            if (item.text != null) binding.emptyListTv.text = item.text
        }
    }
}

data class EmptyListContent(val text: String? = null)