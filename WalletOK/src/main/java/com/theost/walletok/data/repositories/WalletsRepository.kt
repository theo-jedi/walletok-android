package com.theost.walletok.data.repositories

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.CurrencyDto
import com.theost.walletok.data.dto.mapToWallet
import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.models.WalletsOverall
import io.reactivex.Completable
import io.reactivex.Single

object WalletsRepository {
    private val service = WalletOkService.getInstance()
    private val wallets = mutableListOf<Wallet>()
    private val walletsOverall = WalletsOverall(12000000, 10000000, Currency("RUB", 2))

    fun getWallets(): Single<List<Wallet>> {
        return if (wallets.isNotEmpty()) Single.just(wallets) else
            service.getWallets().map { list -> list.map { it.mapToWallet() } }
                .doOnSuccess {
                    wallets.clear()
                    wallets.addAll(it)
                }
    }

    fun getWalletsOverall(): Single<WalletsOverall> {
        return Single.just(walletsOverall)
    }

    fun addWallet(wallet: Wallet): Completable {
        return Completable.fromAction {
            service.addWallet(
                id = wallet.id,
                currency = CurrencyDto(wallet.currency.shortName, wallet.currency.decimalDigits),
                balanceLimit = wallet.loseLimit
            )
        }.doOnComplete {
            wallets.add(wallet)
        }
    }
}