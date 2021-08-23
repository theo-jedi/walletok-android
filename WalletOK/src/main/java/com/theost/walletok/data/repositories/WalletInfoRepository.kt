package com.theost.walletok.data.repositories

import com.theost.walletok.data.models.Wallet
import io.reactivex.Single

object WalletInfoRepository {
    private val walletInfo = Wallet(
        id = 0,
        name = "Кошелек 1",
        currency = "rub",
        amountOfMoney = 0,
        gain = 0,
        lose = 0,
        loseLimit = 0
    )

    fun getWalletInfo(id: Int): Single<Wallet> {
        return Single.just(walletInfo)
    }
}