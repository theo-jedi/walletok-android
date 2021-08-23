package com.theost.walletok.presentation.wallet_details.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.presentation.base.AdapterDelegate
import com.theost.walletok.databinding.ItemEmptyListBinding

class EmptyListAdapterDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemEmptyListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        // Nothing to do
    }

    override fun isOfViewType(item: Any) = item is EmptyListContent

    class ViewHolder(binding: ItemEmptyListBinding) :
        RecyclerView.ViewHolder(binding.root)
}

object EmptyListContent