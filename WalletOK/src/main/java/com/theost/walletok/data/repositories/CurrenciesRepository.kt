package com.theost.walletok.data.repositories

import com.theost.walletok.App
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.db.entities.mapToCurrency
import com.theost.walletok.data.db.entities.mapToEntity
import com.theost.walletok.data.dto.mapToCurrency
import com.theost.walletok.data.models.Currency
import com.theost.walletok.utils.RxResource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object CurrenciesRepository {
    private val service = WalletOkService.getInstance()
    private val currencies = mutableListOf(
        Currency(
            shortName = "RUB",
            decimalDigits = 2
        ),
        Currency(
            shortName = "USD",
            decimalDigits = 2,
        ),
        Currency(
            shortName = "EUR",
            decimalDigits = 2,
        )
    )
    private val currenciesPrices =
        mutableListOf<Pair<String, Double>>(
            Pair("USD", 74.28),
            Pair("EUR", 87.28),
            Pair("JPY", 0.67)
        )

    fun getCurrenciesFromServer(): Single<RxResource<List<Currency>>> {
        return service.getCurrencies()
            .map { list -> list.map { it.mapToCurrency() } }
            .map { RxResource.success(it) }
            .onErrorReturn { RxResource.error(it, null) }
            .doOnSuccess {
                if (it.data != null) {
                    currencies.clear()
                    currencies.addAll(it.data)
                    addCurrenciesToDb(it.data)
                }
            }
            .subscribeOn(Schedulers.io())
    }

    fun getCurrenciesPrices(
        currenciesShortNames: List<String>,
        commonCurrencyShortName: String
    ): Single<RxResource<List<Pair<String, Double>>>> {
        return if (currenciesPrices.isNotEmpty()) Single.just(RxResource.success(currenciesPrices))
        else Single.zip(
            service.convertCurrency(currenciesShortNames[0], commonCurrencyShortName),
            service.convertCurrency(currenciesShortNames[1], commonCurrencyShortName),
            service.convertCurrency(currenciesShortNames[2], commonCurrencyShortName),
            { first, second, third ->
                listOf(
                    Pair(currenciesShortNames[0], first),
                    Pair(currenciesShortNames[1], second),
                    Pair(currenciesShortNames[2], third)
                )
            }
        )
            .map { RxResource.success(it) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

    fun getCurrenciesFromCache(): Single<RxResource<List<Currency>>> {
        return if (!currencies.isNullOrEmpty()) Single.just(currencies)
            .doOnSuccess { addCurrenciesToDb(it) }
            .map { RxResource.success(it) }
        else App.appDatabase.currenciesDao().getAll()
            .map { list ->
                list.map { it.mapToCurrency() }
            }
            .map { RxResource.success(it) }
            .subscribeOn(Schedulers.io())
    }

    fun getCurrencies(): Observable<RxResource<List<Currency>>> {
        return Observable.concat(
            getCurrenciesFromCache().toObservable(),
            getCurrenciesFromServer().toObservable()
        )
    }

    private fun addCurrenciesToDb(currencies: List<Currency>) {
        App.appDatabase.currenciesDao().insertAll(currencies.map {
            it.mapToEntity()
        }).subscribeOn(Schedulers.io()).subscribe()
    }
}