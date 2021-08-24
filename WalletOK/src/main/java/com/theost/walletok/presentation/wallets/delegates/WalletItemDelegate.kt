package com.theost.walletok.presentation.wallets.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.databinding.ItemWalletBinding
import com.theost.walletok.presentation.base.AdapterDelegate
import kotlin.properties.Delegates

class WalletItemDelegate(private val listener: View.OnClickListener) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemWalletBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val holder = ViewHolder(binding)
        holder.itemView.setOnClickListener(listener)
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as WalletContent)
    }

    override fun isOfViewType(item: Any) = item is WalletContent

    class ViewHolder(val binding: ItemWalletBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var walletId by Delegates.notNull<Int>()
        fun bind(item: WalletContent) {
            walletId = item.id
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
