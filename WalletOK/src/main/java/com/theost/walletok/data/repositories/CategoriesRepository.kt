package com.theost.walletok.data.repositories

import com.theost.walletok.App
import com.theost.walletok.R
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.db.entities.mapToEntity
import com.theost.walletok.data.db.entities.mapToTransactionCategory
import com.theost.walletok.data.dto.CategoryPostDto
import com.theost.walletok.data.dto.mapToCategory
import com.theost.walletok.data.models.CategoryCreationModel
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCategoryType
import com.theost.walletok.delegates.CategoryItem
import com.theost.walletok.utils.RxResource
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.random.Random

object CategoriesRepository {
    private val service = WalletOkService.getInstance()
    private val categories = mutableListOf<TransactionCategory>()
    private val defaultCategories = mutableListOf(
        TransactionCategory(
            id = 0,
            userId = null,
            iconLink = R.drawable.ic_category_card,
            iconColor = -16729811,
            name = "Зарплата",
            type = TransactionCategoryType.INCOME
        ),
        TransactionCategory(
            id = 1,
            userId = null,
            iconLink = R.drawable.ic_category_card,
            iconColor = -16729811,
            name = "Подработка",
            type = TransactionCategoryType.INCOME
        ),
        TransactionCategory(
            id = 2,
            userId = null,
            iconLink = R.drawable.ic_category_gift,
            iconColor = -16729811,
            name = "Подарок",
            type = TransactionCategoryType.INCOME
        ),
        TransactionCategory(
            id = 3,
            userId = null,
            iconLink = R.drawable.ic_category_percent,
            iconColor = -16729811,
            name = "Капитализация",
            type = TransactionCategoryType.INCOME
        ),
        TransactionCategory(
            id = 4,
            userId = null,
            iconLink = R.drawable.ic_category_food,
            iconColor = -8952384,
            name = "Кафе и рестораны",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 5,
            userId = null,
            iconLink = R.drawable.ic_category_supermarket,
            iconColor = -13393938,
            name = "Супермаркеты",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 6,
            userId = null,
            iconLink = R.drawable.ic_category_sport,
            iconColor = -6731961,
            name = "Спорт",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 7,
            userId = null,
            iconLink = R.drawable.ic_category_transport,
            iconColor = -1166406,
            name = "Транспорт",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 8,
            userId = null,
            iconLink = R.drawable.ic_category_pharmacy,
            iconColor = -15278991,
            name = "Медицина",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 9,
            userId = null,
            iconLink = R.drawable.ic_category_gas,
            iconColor = -1137869,
            name = "Бензин",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 10,
            userId = null,
            iconLink = R.drawable.ic_category_house,
            iconColor = -7259779,
            name = "Квартплата",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 11,
            userId = null,
            iconLink = R.drawable.ic_category_travel,
            iconColor = -1123533,
            name = "Путешествия",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 12,
            userId = null,
            iconLink = R.drawable.ic_category_jewelry,
            iconColor = -295944,
            name = "Драгоценности",
            type = TransactionCategoryType.EXPENSE
        )
    )

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
            .doOnSuccess {
                if (it.data.isNullOrEmpty()) saveCategoriesToDb(defaultCategories)
                categories.addAll(it.data!!)
            }
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

    private fun deleteCategoriesFromDb(category: TransactionCategory) {
        App.appDatabase.categoriesDao().delete(category.mapToEntity())
            .subscribeOn(Schedulers.io()).subscribe()
    }

    fun addCategory(category: CategoryCreationModel): Completable {
        return Completable.fromAction {
            val transactionCategory = TransactionCategory(
                id = (10000..100000000).random(),
                iconColor = category.color!!,
                iconLink = category.iconUrl!!,
                name = category.name!!,
                type = if (category.type == TransactionCategoryType.INCOME.uiName) TransactionCategoryType.INCOME else TransactionCategoryType.EXPENSE,
                userId = 0
            )
            categories.add(transactionCategory)
            saveCategoriesToDb(listOf(transactionCategory))
        }
    }

    fun removeCategory(category: CategoryItem): Completable {
        return Completable.fromAction {
            val transactionCategory = categories.find { it.id == category.id }
            categories.remove(transactionCategory)
            deleteCategoriesFromDb(transactionCategory!!)
        }
    }
}