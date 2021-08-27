package com.theost.walletok.presentation.wallets.wallet_creation

import android.content.Context
import com.theost.walletok.data.models.Currency
import com.theost.walletok.presentation.base.DelegateItem
import com.theost.walletok.presentation.wallets.delegates.CurrencyItemContent

object WalletCurrencyItemsHelper {
    fun getData(
        context: Context,
        selectedCurrency: Currency?,
        currencies: List<Currency>
    ): List<DelegateItem> {
        val result = mutableListOf<DelegateItem>()
        currencies.forEach {
            result.add(
                CurrencyItemContent(
                    currency = it,
                    currencyName = context.resources.getString(it.longNameResId),
                    isSelected = selectedCurrency == it
                )
            )
        }
        return result
    }
}