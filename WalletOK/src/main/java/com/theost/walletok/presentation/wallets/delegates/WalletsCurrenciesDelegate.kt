package com.theost.walletok.presentation.wallets.delegates

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.R
import com.theost.walletok.databinding.ItemCurrenciesCardBinding
import com.theost.walletok.presentation.base.AdapterDelegate

class WalletsCurrenciesDelegate : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemCurrenciesCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as CurrenciesContent)
    }

    override fun isOfViewType(item: Any) = item is CurrenciesContent

    class ViewHolder(val binding: ItemCurrenciesCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CurrenciesContent) {
            binding.currencyLeft.currencyNameTv.text = item.currencies[0].name
            binding.currencyCenter.currencyNameTv.text = item.currencies[1].name
            binding.currencyRight.currencyNameTv.text = item.currencies[2].name
            binding.currencyLeft.currencyValueTv.text = item.currencies[0].price
            binding.currencyCenter.currencyValueTv.text = item.currencies[1].price
            binding.currencyRight.currencyValueTv.text = item.currencies[2].price
            binding.currencyLeft.currencyArrowIcon.setImageResource(
                if (item.currencies[0].isGrowing)
                    R.drawable.ic_green_arrow_up else R.drawable.ic_red_arrow_down
            )
            binding.currencyCenter.currencyArrowIcon.setImageResource(
                if (item.currencies[1].isGrowing)
                    R.drawable.ic_green_arrow_up else R.drawable.ic_red_arrow_down
            )
            binding.currencyRight.currencyArrowIcon.setImageResource(
                if (item.currencies[2].isGrowing)
                    R.drawable.ic_green_arrow_up else R.drawable.ic_red_arrow_down
            )
        }
    }
}

data class CurrenciesContent(
    val currencies: List<CurrencyContent>
)

class CurrencyContent(val name: String, val price: String, val isGrowing: Boolean)
