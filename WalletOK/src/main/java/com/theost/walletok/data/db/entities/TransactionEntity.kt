package com.theost.walletok.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.walletok.data.dto.TransactionsDto
import com.theost.walletok.data.models.Transaction

@Entity(tableName = "transactions")
class TransactionEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "wallet_id")
    val walletId: Int,
    @ColumnInfo(name = "category_id")
    val categoryId: Int,
    @ColumnInfo(name = "amount")
    val amount: Long,
    @ColumnInfo(name = "date")
    val date: String
)

fun TransactionEntity.mapToTransaction(): Transaction {
    return Transaction(
        id = this.id,
        categoryId = this.categoryId,
        dateTime = TransactionsDto.dateTimeFormat.parse(this.date)!!,
        money = this.amount,
        walletId = this.walletId
    )
}