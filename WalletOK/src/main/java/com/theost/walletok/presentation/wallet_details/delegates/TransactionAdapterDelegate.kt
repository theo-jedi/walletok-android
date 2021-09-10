package com.theost.walletok.presentation.wallet_details.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoaderFactory
import coil.load
import com.theost.walletok.App
import com.theost.walletok.databinding.ItemTransactionBinding
import com.theost.walletok.presentation.base.AdapterDelegate
import com.theost.walletok.utils.ViewUtils
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
            if (item.iconColor != null && item.iconUrl != null) {
                transactionId = item.transactionId
                binding.transactionIcon.setImageResource(item.iconUrl)
                binding.transactionCategoryCircle.setColorFilter(item.iconColor, android.graphics.PorterDuff.Mode.SRC_IN)
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
    val categoryName: String,
    val transactionType: String,
    val moneyAmount: String,
    val time: String,
    val iconUrl: Int?,
    val iconColor: Int?
)