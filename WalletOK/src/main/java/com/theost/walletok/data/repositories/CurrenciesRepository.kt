package com.theost.walletok.data.repositories

import com.theost.walletok.App
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.db.entities.CurrencyEntity
import com.theost.walletok.data.db.entities.mapToCurrency
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
                addCurrenciesToDb(it)
            }
    }

    private fun addCurrenciesToDb(currencies: List<Currency>) {
        App.appDatabase.currenciesDao().insertAll(currencies.map {
            CurrencyEntity(
                shortName = it.shortName,
                decimalDigits = it.decimalDigits
            )
        })
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun getCurrenciesFromCache(): Single<List<Currency>> {
        return if (!currencies.isNullOrEmpty()) Single.just(currencies)
        else App.appDatabase.currenciesDao().getAll()
            .map { list -> list.map { it.mapToCurrency() } }
    }

    fun getCurrencies(): Observable<List<Currency>> {
        return Observable.concat(
            getCurrenciesFromCache().toObservable(),
            getCurrenciesFromServer().toObservable()
        )
    }
}