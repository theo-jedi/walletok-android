package com.theost.walletok.data.repositories

import com.theost.walletok.data.models.WalletInfo
import io.reactivex.Single

object WalletInfoRepository {
    private val walletInfo = WalletInfo(
        name = "Кошелек 1",
        currency = "rub",
        amountOfMoney = 0,
        gain = 0,
        lose = 0,
        loseLimit = 0
    )

    fun getWalletInfo(): Single<WalletInfo> {
        return Single.just(walletInfo)
    }
}