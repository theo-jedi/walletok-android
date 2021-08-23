package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.presentation.base.AdapterDelegate
import com.theost.walletok.databinding.ItemWalletInfoBinding

class WalletDetailsHeaderAdapterDelegate :
    AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemWalletInfoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: Any,
        position: Int
    ) {
        (holder as ViewHolder).bind(item as HeaderContent)
    }

    override fun isOfViewType(item: Any) = item is HeaderContent

    class ViewHolder(private val binding: ItemWalletInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HeaderContent) {
            binding.walletNameTextView.text = item.walletName
            binding.receivedMoneyTextView.text = item.walletGain
            binding.spentMoneyTextView.text = item.walletLose
            binding.walletMoneyTextView.text = item.walletMoney
        }
    }
}

data class HeaderContent(
    val walletName: String,
    val walletGain: String,
    val walletLose: String,
    val walletLoseLimit: String,
    val walletMoney: String
)