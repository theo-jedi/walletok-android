package com.theost.walletok.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Transaction(
    val id: Int,
    val categoryId: Int,
    val money: Long,
    val dateTime: Date
) : Parcelable

data class TransactionCategory(
    val id: Int,
    val iconColor: Int,
    val iconLink: String,
    val name: String,
    val type: TransactionCategoryType
)

enum class TransactionCategoryType(val uiName: String) {
    INCOME("Пополнение"), EXPENSE("Траты")
}