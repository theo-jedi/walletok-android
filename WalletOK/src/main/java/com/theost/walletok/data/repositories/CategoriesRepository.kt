package com.theost.walletok.data.repositories

import com.theost.walletok.App
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.db.entities.CategoryEntity
import com.theost.walletok.data.db.entities.mapToTransactionCategory
import com.theost.walletok.data.dto.mapToCategory
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCategoryType
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
            .doOnSuccess { list ->
                categories.clear()
                categories.addAll(list)
                saveCategoriesToDb(list)
            }
    }

    fun getCategoriesFromCache(): Single<List<TransactionCategory>> {
        return if (!categories.isNullOrEmpty()) Single.just(categories)
        else App.appDatabase.categoriesDao().getAll()
            .map { list -> list.map { it.mapToTransactionCategory() } }
            .subscribeOn(Schedulers.io())
    }

    fun getCategories(): Observable<List<TransactionCategory>> {
        return Observable.concat(
            getCategoriesFromCache().toObservable(),
            getCategoriesFromServer().toObservable()
        )
    }

    private fun saveCategoriesToDb(categories: List<TransactionCategory>) {
        App.appDatabase.categoriesDao().insertAll(categories.map {
            CategoryEntity(
                id = it.id,
                iconColor = it.iconColor,
                iconLink = it.iconLink,
                income = it.type == TransactionCategoryType.INCOME,
                name = it.name,
                userId = it.userId
            )
        }).subscribeOn(Schedulers.io()).subscribe()
    }
}