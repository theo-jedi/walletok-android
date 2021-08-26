package com.theost.walletok.presentation.wallets

import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.models.WalletsOverall
import com.theost.walletok.presentation.base.delegates.EmptyListContent
import com.theost.walletok.presentation.wallets.delegates.CurrenciesContent
import com.theost.walletok.presentation.wallets.delegates.CurrencyContent
import com.theost.walletok.presentation.wallets.delegates.WalletContent
import com.theost.walletok.presentation.wallets.delegates.WalletsOverallContent
import com.theost.walletok.utils.StringUtils
import kotlin.math.roundToLong

object WalletsItemHelper {

    fun getData(
        wallets: List<Wallet>,
        walletsOverall: WalletsOverall,
        currenciesPrices: List<Pair<String, Double>>?
    ): List<Any> {
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
        if (currenciesPrices != null)
            result.add(
                CurrenciesContent(
                    listOf(
                        CurrencyContent(
                            currenciesPrices[0].first,
                            ((currenciesPrices[0].second * 100).roundToLong() / 100.0).toString(),
                            true
                        ),
                        CurrencyContent(
                            currenciesPrices[1].first,
                            ((currenciesPrices[1].second * 100).roundToLong() / 100.0).toString(),
                            true
                        ),
                        CurrencyContent(
                            currenciesPrices[2].first,
                            ((currenciesPrices[2].second * 100).roundToLong() / 100.0).toString(),
                            false
                        )
                    )
                )
            )
        result.addAll(wallets.map { wallet ->
            WalletContent(
                id = wallet.id,
                name = wallet.name,
                money = "${StringUtils.convertMoneyForDisplay(wallet.amountOfMoney)} " +
                        wallet.currency.symbol,
                isLimitExceeded = wallet.loseLimit != null && (wallet.loseLimit < wallet.lose)
            )
        })
        if (wallets.isEmpty())
            result.add(EmptyListContent("У вас пока нет созданных кошельков"))
        return result
    }
}