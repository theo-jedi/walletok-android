package com.theost.walletok.data.models

import java.util.*

data class Transaction(
    val id: Int,
    val categoryId: Int,
    val money: Int,
    val currency: Currency,
    val dateTime: Date
)

data class TransactionCategory(
    val id: Int,
    val image: Any,
    val name: String,
    val type: TransactionCategoryType
)

enum class TransactionCategoryType(val uiName: String) {
    INCOME("Пополнение"), EXPENSE("Траты")
}