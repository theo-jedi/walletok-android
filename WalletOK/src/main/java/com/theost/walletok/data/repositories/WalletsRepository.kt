package com.theost.walletok.data.repositories

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.mapToWallet
import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.Wallet
import io.reactivex.Single

object WalletsRepository {
    private val service = WalletOkService.getInstance()
    private val wallets = mutableListOf(
        Wallet(
            id = 0,
            name = "Кошелек 1",
            currency = Currency.RUB,
            amountOfMoney = 0,
            gain = 0,
            lose = 0,
            loseLimit = 0
        )
    )

    fun getWallets(): Single<List<Wallet>> {
        return if (wallets.isNotEmpty()) Single.just(wallets) else
            service.getWallets().map { list -> list.map { it.mapToWallet() } }
                .doOnSuccess {
                    wallets.clear()
                    wallets.addAll(it)
                }
    }
}