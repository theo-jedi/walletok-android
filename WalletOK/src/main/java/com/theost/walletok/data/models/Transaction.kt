package com.theost.walletok.data.models

import java.util.*

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val id: Int,
    val categoryId: Int,
    val money: Long,
    val currency: Currency,
    val dateTime: Date
) : Parcelable

data class TransactionCategory(
    val id: Int,
    val image: Any,
    val name: String,
    val type: TransactionCategoryType
)

enum class TransactionCategoryType(val uiName: String) {
    INCOME("Пополнение"), EXPENSE("Траты")
}