package com.theost.walletok.presentation.wallets.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.databinding.ItemWalletBinding
import com.theost.walletok.presentation.base.AdapterDelegate

class WalletItemDelegate(private val clickListener: (WalletContent) -> Unit) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemWalletBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as WalletContent)
    }

    override fun isOfViewType(item: Any) = item is WalletContent

    class ViewHolder(val binding: ItemWalletBinding, val clickListener: (WalletContent) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WalletContent) {
            itemView.setOnClickListener { clickListener.invoke(item) }
            binding.walletNameTv.text = item.name
            binding.walletMoneyAmountTv.text = item.money
            if (item.isLimitExceeded)
                binding.limitExceededCard.visibility = View.VISIBLE
        }
    }
}

data class WalletContent(
    val id: Int,
    val name: String,
    val money: String,
    val isLimitExceeded: Boolean
)
