package com.theost.walletok.presentation.wallets.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.databinding.ItemWalletsHeaderBinding
import com.theost.walletok.presentation.base.AdapterDelegate

class WalletsHeaderDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemWalletsHeaderBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as WalletsOverallContent)
    }

    override fun isOfViewType(item: Any) = item is WalletsOverallContent

    class ViewHolder(val binding: ItemWalletsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WalletsOverallContent) {
            binding.overallBalanceValueTv.text = item.overallMoney
            binding.overallIncomeValueTv.text = item.overallIncome
            binding.overallExpenseValueTv.text = item.overallExpense
        }
    }
}

data class WalletsOverallContent(
    val overallIncome: String,
    val overallExpense: String,
    val overallMoney: String
)