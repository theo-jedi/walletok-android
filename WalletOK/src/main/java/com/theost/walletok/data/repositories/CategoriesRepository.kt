package com.theost.walletok.data.repositories

import com.theost.walletok.R
import com.theost.walletok.data.TransactionCategory
import com.theost.walletok.data.TransactionCategoryType

object CategoriesRepository {
    private val categories = mutableListOf(
        TransactionCategory(
            id = 0,
            image = R.drawable.ic_category_card,
            name = "Зарплата",
            type = TransactionCategoryType.INCOME
        )
    )

    fun getCategories(): List<TransactionCategory> = categories
}