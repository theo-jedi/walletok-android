package com.theost.walletok.delegates

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.AdapterDelegate
import com.theost.walletok.databinding.ItemTransactionBinding

class TransactionAdapterDelegate(val listener: View.OnClickListener) :
    AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.setOnClickListener(listener)
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
            when (item.image) {
                is Int -> binding.transactionIconImage.setImageResource(item.image)
                is Uri -> binding.transactionIconImage.setImageURI(item.image)
            }
            binding.transactionNameTextView.text = item.categoryName
            binding.transactionTypeTextView.text = item.transactionType
            binding.transactionTimeTextView.text = item.time
            binding.transactionMoneyAmountTextView.text = item.moneyAmount
        }
    }
}

data class TransactionContent(
    val image: Any,
    val categoryName: String,
    val transactionType: String,
    val moneyAmount: String,
    val time: String
)