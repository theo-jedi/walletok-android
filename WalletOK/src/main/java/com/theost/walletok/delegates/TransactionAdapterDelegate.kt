package com.theost.walletok.delegates

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.AdapterDelegate
import com.theost.walletok.databinding.ItemTransactionBinding

class TransactionAdapterDelegate :
    AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.setOnClickListener {
            Log.d("HELP", "onCreateViewHolder: hello")
        }
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: Any,
        position: Int
    ) {
        (holder as ViewHolder).bind(item as TransactionContent)
    }

    override fun isOfViewType(item: Any) = item is TransactionContent

    class ViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: TransactionContent) {
            binding.transactionIconImage.setImageResource(item.iconResId)
            binding.transactionNameTextView.text = item.categoryName
            binding.transactionTypeTextView.text = item.transactionType
            binding.transactionTimeTextView.text = item.time
            binding.transactionMoneyAmountTextView.text = item.moneyAmount
        }
    }
}

data class TransactionContent(
    val iconResId: Int,
    val categoryName: String,
    val transactionType: String,
    val moneyAmount: String,
    val time: String,
    val date: String
)