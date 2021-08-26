package com.theost.walletok.data.repositories

import com.theost.walletok.App
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.db.entities.WalletEntity
import com.theost.walletok.data.db.entities.mapToCurrency
import com.theost.walletok.data.db.entities.mapToWallet
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
    private val walletsOverall: WalletsOverall? =
        WalletsOverall(12000000, 10000000, Currency("RUB", 2))

    fun getWalletsFromCache(): Single<List<Wallet>> {
        return if (!wallets.isNullOrEmpty()) Single.just(wallets) else
            Single.zip(
                App.appDatabase.walletsDao().getAll(),
                App.appDatabase.currenciesDao().getAll(),
                { walletEntities, currencyEntities ->
                    if (walletEntities.isNotEmpty() && currencyEntities.isNotEmpty()) {
                        val currencies = currencyEntities.map { it.mapToCurrency() }
                        val wallets =
                            walletEntities.map { walletEntity ->
                                walletEntity.mapToWallet(
                                    currencies.find {
                                        it.shortName == walletEntity.currencyShortName
                                    }!!
                                )
                            }
                        wallets
                    } else listOf()
                }).subscribeOn(Schedulers.io())
    }

    fun getWalletsFromServer(): Single<List<Wallet>> {
        return service.getWallets().map { list -> list.map { it.mapToWallet() } }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                wallets.clear()
                wallets.addAll(it)
                addWalletsToDb(it)
            }
    }

    fun getWallets(): Observable<List<Wallet>> {
        return Observable.concat(
            getWalletsFromCache().toObservable(),
            getWalletsFromServer().toObservable()
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
                .doOnSuccess {
                    wallets.add(it.mapToWallet())
                    addWalletsToDb(listOf(it.mapToWallet()))
                }
        )
    }

    fun addWalletsToDb(wallets: List<Wallet>) {
        App.appDatabase.walletsDao().insertAll(
            wallets.map {
                WalletEntity(
                    name = it.name,
                    balanceLimit = it.loseLimit,
                    currencyShortName = it.currency.shortName,
                    income = it.gain,
                    expenditure = it.lose,
                    id = it.id
                )
            }
        ).subscribeOn(Schedulers.io()).subscribe()
    }
}