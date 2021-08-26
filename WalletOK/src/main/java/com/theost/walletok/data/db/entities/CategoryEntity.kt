package com.theost.walletok.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCategoryType

@Entity(tableName = "categories")
class CategoryEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "icon_color")
    val iconColor: Int,
    @ColumnInfo(name = "icon_link")
    val iconLink: String,
    @ColumnInfo(name = "income")
    val income: Boolean,
    @ColumnInfo(name = "user_id")
    val userId: Int?
)

fun CategoryEntity.mapToTransactionCategory(): TransactionCategory {
    return TransactionCategory(
        id = this.id,
        iconColor = this.iconColor,
        iconLink = this.iconLink,
        name = this.name,
        type = if (this.income) TransactionCategoryType.INCOME else TransactionCategoryType.EXPENSE,
        userId = this.userId
    )
}