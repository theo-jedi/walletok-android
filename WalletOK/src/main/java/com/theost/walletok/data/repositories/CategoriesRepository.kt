package com.theost.walletok.data.repositories

import com.theost.walletok.App
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.db.entities.mapToEntity
import com.theost.walletok.data.db.entities.mapToTransactionCategory
import com.theost.walletok.data.dto.mapToCategory
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.utils.RxResource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object CategoriesRepository {
    private val service = WalletOkService.getInstance()
    private val categories = mutableListOf<TransactionCategory>()

    fun getCategoriesFromServer(): Single<RxResource<List<TransactionCategory>>> {
        return service.getCategories()
            .map { list -> list.map { it.mapToCategory() } }
            .map { RxResource.success(it) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
            .doOnSuccess { resource ->
                if (resource.data != null) {
                    categories.clear()
                    categories.addAll(resource.data)
                    saveCategoriesToDb(resource.data)
                }
            }
    }

    fun getCategoriesFromCache(): Single<RxResource<List<TransactionCategory>>> {
        return if (!categories.isNullOrEmpty()) Single.just(categories)
            .map { RxResource.success(it) }
        else App.appDatabase.categoriesDao().getAll()
            .map { list -> list.map { it.mapToTransactionCategory() } }
            .map { RxResource.success(it) }
            .subscribeOn(Schedulers.io())
    }

    fun getCategories(): Observable<RxResource<List<TransactionCategory>>> {
        return Observable.concat(
            getCategoriesFromCache().toObservable(),
            getCategoriesFromServer().toObservable()
        )
    }

    private fun saveCategoriesToDb(categories: List<TransactionCategory>) {
        App.appDatabase.categoriesDao().insertAll(categories.map {
            it.mapToEntity()
        }).subscribeOn(Schedulers.io()).subscribe()
    }
}