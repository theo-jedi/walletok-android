package com.theost.walletok.presentation.wallets

import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.models.WalletsOverall
import com.theost.walletok.presentation.base.delegates.EmptyListContent
import com.theost.walletok.presentation.wallets.delegates.CurrenciesContent
import com.theost.walletok.presentation.wallets.delegates.CurrencyContent
import com.theost.walletok.presentation.wallets.delegates.WalletContent
import com.theost.walletok.presentation.wallets.delegates.WalletsOverallContent
import com.theost.walletok.utils.StringUtils

object WalletsItemHelper {
    const val walletsListOffset = 2
    fun getData(wallets: List<Wallet>, walletsOverall: WalletsOverall): List<Any> {
        val result = mutableListOf<Any>()
        result.add(
            WalletsOverallContent(
                overallIncome = "${StringUtils.convertMoneyForDisplay(walletsOverall.totalIncome)} " +
                        walletsOverall.currency.symbol,
                overallExpense = "${StringUtils.convertMoneyForDisplay(walletsOverall.totalExpense)} " +
                        walletsOverall.currency.symbol,
                overallMoney = "${StringUtils.convertMoneyForDisplay(walletsOverall.totalIncome - walletsOverall.totalExpense)} " +
                        walletsOverall.currency.symbol
            )
        )
        result.add(
            CurrenciesContent(
                listOf(
                    CurrencyContent("USD", "31.42", true),
                    CurrencyContent("EUR", "41.14", true),
                    CurrencyContent("CNY", "5.36", false)
                )
            )
        )
        result.addAll(wallets.map { wallet ->
            WalletContent(
                id = wallet.id,
                name = wallet.name,
                money = "${StringUtils.convertMoneyForDisplay(wallet.amountOfMoney)} " +
                        wallet.currency.symbol,
                isLimitExceeded = (wallet.loseLimit < wallet.lose)
            )
        })
        if (wallets.isEmpty())
            result.add(EmptyListContent("У вас пока нет созданных кошельков"))
        return result
    }
}