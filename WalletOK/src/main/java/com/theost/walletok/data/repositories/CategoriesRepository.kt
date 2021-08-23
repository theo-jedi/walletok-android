package com.theost.walletok.data.repositories

import com.theost.walletok.R
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.mapToCategory
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCategoryType
import io.reactivex.Single

object CategoriesRepository {
    private val service = WalletOkService.getInstance()
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
        )
    )

    fun getCategories(): Single<List<TransactionCategory>> {
        return if (categories.isNotEmpty()) Single.just(categories) else
            service.getCategories().map { list -> list.map { it.mapToCategory() } }
                .doOnSuccess {
                    categories.clear()
                    categories.addAll(it)
                }
    }
}