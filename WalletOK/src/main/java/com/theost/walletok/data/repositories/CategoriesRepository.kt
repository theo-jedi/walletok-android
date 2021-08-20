package com.theost.walletok.data.repositories

import com.theost.walletok.R
import com.theost.walletok.data.TransactionCategory
import com.theost.walletok.data.TransactionCategoryType
import io.reactivex.Single

object CategoriesRepository {
    private val categories = mutableListOf(
        TransactionCategory(
            id = 0,
            image = R.drawable.ic_category_card,
            name = "Зарплата",
            type = TransactionCategoryType.INCOME
        )
    )

    fun getCategories(): Single<List<TransactionCategory>> {
        return Single.just(categories)
    }
}