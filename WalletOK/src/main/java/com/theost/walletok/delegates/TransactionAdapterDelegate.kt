package com.theost.walletok.delegates

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.base.AdapterDelegate
import com.theost.walletok.databinding.ItemTransactionBinding
import kotlin.properties.Delegates

class TransactionAdapterDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
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
        var transactionId by Delegates.notNull<Int>()
        fun bind(item: TransactionContent) {
            transactionId = item.transactionId
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
    val transactionId: Int,
    val image: Any,
    val categoryName: String,
    val transactionType: String,
    val moneyAmount: String,
    val time: String
)