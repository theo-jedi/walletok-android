package com.theost.walletok.data.repositories

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.mapToCurrency
import com.theost.walletok.data.models.Currency
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object CurrenciesRepository {
    private val service = WalletOkService.getInstance()
    private val currencies = mutableListOf<Currency>()

    fun getCurrenciesFromServer(): Single<List<Currency>> {
        return service.getCurrencies()
            .map { list -> list.map { it.mapToCurrency() } }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                currencies.clear()
                currencies.addAll(it)
            }
    }

    fun getCurrenciesFromCache(): Single<List<Currency>> {
        return Single.just(currencies)
    }

    fun getCurrencies(): Observable<List<Currency>> {
        return Observable.concat(
            getCurrenciesFromCache().toObservable(),
            getCurrenciesFromServer().toObservable()
        )
    }
}