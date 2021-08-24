package com.theost.walletok.data.repositories

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.mapToCategory
import com.theost.walletok.data.models.TransactionCategory
import io.reactivex.Single

object CategoriesRepository {
    private val service = WalletOkService.getInstance()
    private val categories = mutableListOf<TransactionCategory>()

    fun getCategories(): Single<List<TransactionCategory>> {
        return if (categories.isNotEmpty()) Single.just(categories) else
            service.getCategories().map { list -> list.map { it.mapToCategory() } }
                .doOnSuccess {
                    categories.clear()
                    categories.addAll(it)
                }
    }
}