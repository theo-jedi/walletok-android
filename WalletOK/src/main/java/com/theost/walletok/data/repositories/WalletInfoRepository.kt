package com.theost.walletok.data.repositories

import com.theost.walletok.data.WalletInfo

object WalletInfoRepository {
    private val walletInfo = WalletInfo(
        name = "Кошелек 1",
        currency = "rub",
        amountOfMoney = "0.0",
        gain = "0.0",
        lose = "0.0",
        loseLimit = "15000.0"
    )

    fun getWalletInfo() = walletInfo
}