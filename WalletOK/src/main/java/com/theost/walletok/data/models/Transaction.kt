package com.theost.walletok.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val id: Int,
    val categoryId: Int,
    val money: Int,
    val currency: String,
    val dateTime: String // yyyy/MM/dd hh:mm
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