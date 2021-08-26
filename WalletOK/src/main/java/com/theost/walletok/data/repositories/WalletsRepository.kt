package com.theost.walletok.data.repositories

import android.util.Log
import com.theost.walletok.App
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.db.entities.mapToCurrency
import com.theost.walletok.data.db.entities.mapToEntity
import com.theost.walletok.data.db.entities.mapToWallet
import com.theost.walletok.data.dto.CurrencyPostDto
import com.theost.walletok.data.dto.WalletPostDto
import com.theost.walletok.data.dto.mapToWallet
import com.theost.walletok.data.dto.mapToWalletsOverall
import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.models.WalletCreationModel
import com.theost.walletok.data.models.WalletsOverall
import com.theost.walletok.utils.RxResource
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object WalletsRepository {
    private val service = WalletOkService.getInstance()
    private val wallets = mutableListOf<Wallet>()
    private var walletsOverall = WalletsOverall(0, 0, Currency("RUB", 2))

    fun getWalletsFromCache(): Single<RxResource<List<Wallet>>> {
        return if (!wallets.isNullOrEmpty()) Single.just(RxResource.success(wallets)) else
            Single.zip(
                App.appDatabase.walletsDao().getAll(),
                App.appDatabase.currenciesDao().getAll(),
                { walletEntities, currencyEntities ->
                    Log.d("HELP", "getWalletsFromCache: $walletEntities $currencyEntities")
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
                }).map { RxResource.success(it) }
    }

    fun getWalletsFromServer(): Single<RxResource<List<Wallet>>> {
        return service.getWallets().map { list -> list.map { it.mapToWallet() } }
            .map { RxResource.success(it) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    wallets.clear()
                    wallets.addAll(it.data)
                    addWalletsToDb(it.data)
                }
            }
    }

    fun getWallets(): Observable<RxResource<List<Wallet>>> {
        return Single.concat(
            getWalletsFromCache(),
            getWalletsFromServer()
        ).toObservable()
    }

    fun getWalletsOverallFromCache(): Single<RxResource<WalletsOverall>> {
        return Single.just(walletsOverall).map { RxResource.success(it) }
    }

    fun getWalletsOverallFromServer(): Single<RxResource<WalletsOverall>> {
        return service.getWalletsStat().map { RxResource.success(it.mapToWalletsOverall()) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess { if (it.data != null) walletsOverall = it.data }
    }

    fun getWalletsOverall(): Observable<RxResource<WalletsOverall>> {
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
            wallets.map { it.mapToEntity() }
        ).subscribeOn(Schedulers.io()).subscribe()
    }
}