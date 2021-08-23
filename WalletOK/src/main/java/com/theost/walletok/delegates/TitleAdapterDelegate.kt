package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.presentation.base.AdapterDelegate
import com.theost.walletok.databinding.ItemListTitleBinding

class TitleAdapterDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemListTitleBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as ListTitle)
    }

    override fun isOfViewType(item: Any): Boolean = item is ListTitle

    class ViewHolder(private val binding: ItemListTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(title: ListTitle) {
            binding.listTitle.text = title.name
        }

    }

}

data class ListTitle(
    val name: String
)