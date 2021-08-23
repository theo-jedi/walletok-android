package com.theost.walletok.data.repositories

import com.theost.walletok.R
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCategoryType
import io.reactivex.Single

object CategoriesRepository {
    private val categories = mutableListOf(
        TransactionCategory(
            id = 0,
            image = R.drawable.ic_category_card,
            name = "Зарплата",
            type = TransactionCategoryType.INCOME
        ),
        TransactionCategory(
            id = 1,
            image = R.drawable.ic_category_card,
            name = "Подработка",
            type = TransactionCategoryType.INCOME
        ),
        TransactionCategory(
            id = 2,
            image = R.drawable.ic_category_gift,
            name = "Подарок",
            type = TransactionCategoryType.INCOME
        ),
        TransactionCategory(
            id = 3,
            image = R.drawable.ic_category_percent,
            name = "Капитализация",
            type = TransactionCategoryType.INCOME
        ),
        TransactionCategory(
            id = 4,
            image = R.drawable.ic_category_food,
            name = "Кафе и рестораны",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 5,
            image = R.drawable.ic_category_supermarket,
            name = "Супермаркеты",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 6,
            image = R.drawable.ic_category_sport,
            name = "Спорт",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 7,
            image = R.drawable.ic_category_transport,
            name = "Транспорт",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 8,
            image = R.drawable.ic_category_pharmacy,
            name = "Медицина",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 9,
            image = R.drawable.ic_category_gas,
            name = "Бензин",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 10,
            image = R.drawable.ic_category_house,
            name = "Квартплата",
            type = TransactionCategoryType.EXPENSE
        ),
        TransactionCategory(
            id = 11,
            image = R.drawable.ic_category_travel,
            name = "Путешествия",
            type = TransactionCategoryType.EXPENSE
        )
    )

    fun getCategories(): Single<List<TransactionCategory>> {
        return Single.just(categories)
    }
}