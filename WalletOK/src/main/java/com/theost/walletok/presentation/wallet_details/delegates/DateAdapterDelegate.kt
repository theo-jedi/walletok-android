package com.theost.walletok.presentation.wallet_details.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.presentation.base.AdapterDelegate
import com.theost.walletok.databinding.ItemDateBinding

class DateAdapterDelegate :
    AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemDateBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: Any,
        position: Int
    ) {
        (holder as ViewHolder).bind(item as DateContent)
    }

    override fun isOfViewType(item: Any) = item is DateContent

    class ViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(dateContent: DateContent) {
            binding.dateTextView.text = dateContent.date
        }
    }
}

data class DateContent(val date: String)