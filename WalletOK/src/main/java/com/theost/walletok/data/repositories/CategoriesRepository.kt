package com.theost.walletok.data.repositories

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.mapToCategory
import com.theost.walletok.data.models.TransactionCategory
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object CategoriesRepository {
    private val service = WalletOkService.getInstance()
    private val categories = mutableListOf<TransactionCategory>()

    fun getCategoriesFromServer(): Single<List<TransactionCategory>> {
        return service.getCategories()
            .map { list -> list.map { it.mapToCategory() } }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                categories.clear()
                categories.addAll(it)
            }
    }

    fun getCategoriesFromCache(): Single<List<TransactionCategory>> {
        return Single.just(categories)
    }

    fun getCategories(): Observable<List<TransactionCategory>> {
        return Observable.concat(
            getCategoriesFromCache().toObservable(),
            getCategoriesFromServer().toObservable()
        )
    }
}