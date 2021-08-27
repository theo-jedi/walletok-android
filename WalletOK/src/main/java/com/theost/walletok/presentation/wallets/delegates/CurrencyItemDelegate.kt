package com.theost.walletok.presentation.wallets.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.data.models.Currency
import com.theost.walletok.databinding.ItemListCurrencyBinding
import com.theost.walletok.presentation.base.AdapterDelegate
import com.theost.walletok.presentation.base.DelegateItem

class CurrencyItemDelegate(private val onClick: (Currency) -> Unit) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding =
            ItemListCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as CurrencyItemContent)
    }

    override fun isOfViewType(item: Any) = item is CurrencyItemContent

    class ViewHolder(val binding: ItemListCurrencyBinding, private val onCLick: (Currency) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrencyItemContent) {
            binding.currencyNameTv.text = item.currencyName
            binding.currencySwitch.isChecked = item.isSelected
            binding.currencySwitch.setOnClickListener { onCLick.invoke(item.currency) }
        }
    }
}

data class CurrencyItemContent(
    val currency: Currency,
    val currencyName: String,
    val isSelected: Boolean = false
) : DelegateItem {
    override fun content(): Any = isSelected

    override fun id(): Any = currency

}