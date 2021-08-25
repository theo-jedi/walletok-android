package com.theost.walletok.data.repositories

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.CurrencyPostDto
import com.theost.walletok.data.dto.WalletPostDto
import com.theost.walletok.data.dto.mapToWallet
import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.models.WalletCreationModel
import com.theost.walletok.data.models.WalletsOverall
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object WalletsRepository {
    private val service = WalletOkService.getInstance()
    private val wallets = mutableListOf<Wallet>()
    private val walletsOverall = WalletsOverall(12000000, 10000000, Currency("RUB", 2))

    fun getWalletsFromCache(): Single<List<Wallet>> {
        return Single.just(wallets)
    }

    fun getWalletsFromServer(): Single<List<Wallet>> {
        return service.getWallets().map { list -> list.map { it.mapToWallet() } }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                wallets.clear()
                wallets.addAll(it)
            }
    }

    fun getWallets(): Observable<List<Wallet>> {
        return Observable.concat(
            getWalletsFromCache().toObservable(),
            getWalletsFromServer().toObservable()
        )
    }

    fun getWalletFromCache(walletId: Int): Single<Wallet> {
        return Single.just(wallets.find { it.id == walletId })
    }

    fun getWalletFromServer(walletId: Int): Single<Wallet> {
        return service.getWallet(walletId)
            .map { it.mapToWallet() }
            .subscribeOn(Schedulers.io())
            .doOnSuccess { wallet ->
                val oldWallet = wallets.find { it.id == walletId }
                if (oldWallet != null)
                    wallets[wallets.indexOf(oldWallet)] = wallet
                else wallets.add(wallet)
            }
    }

    fun getWallet(walletId: Int): Observable<Wallet> {
        return Observable.concat(
            getWalletFromCache(walletId).toObservable(),
            getWalletFromServer(walletId).toObservable()
        )
    }

    fun getWalletsOverallFromCache(): Single<WalletsOverall> {
        return Single.just(walletsOverall)
    }

    fun getWalletsOverallFromServer(): Single<WalletsOverall> {
        return Single.just(walletsOverall)
    }

    fun getWalletsOverall(): Observable<WalletsOverall> {
        return Observable.concat(
            getWalletsOverallFromCache().toObservable(),
            getWalletsOverallFromServer().toObservable()
        )
    }

    fun addWallet(walletCreationModel: WalletCreationModel): Completable {
        return Completable.fromSingle(
            service.addWallet(
                WalletPostDto(
                    currency = CurrencyPostDto(
                        walletCreationModel.currency!!.shortName
                    ),
                    balanceLimit = walletCreationModel.balanceLimit,
                    name = walletCreationModel.name
                )
            )
                .subscribeOn(Schedulers.io())
        )
    }
}