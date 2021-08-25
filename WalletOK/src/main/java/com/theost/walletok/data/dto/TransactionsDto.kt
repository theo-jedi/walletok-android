package com.theost.walletok.data.dto

import com.theost.walletok.data.dto.TransactionsDto.Companion.dateTimeFormat
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionsAndLastId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class TransactionsDto(
    @SerialName("next_transaction_id") val nextTransactionId: Int?,
    @SerialName("transactions") val transactions: List<TransactionContentDto>
) {
    companion object {
        val dateTimeFormat =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ru", "RU"))
    }
}

@Serializable
data class TransactionContentDto(
    @SerialName("id") val id: Int,
    @SerialName("walletId") val walletId: Int,
    @SerialName("categoryId") val categoryId: Int,
    @SerialName("amount") val money: Long,
    @SerialName("date") val dateTime: String
)

fun TransactionsDto.mapToTransactionsAndNextId(): TransactionsAndLastId {
    val newList = this.transactions.map {
        it.mapToTransaction()
    }
    return TransactionsAndLastId(newList, this.nextTransactionId)
}

fun TransactionContentDto.mapToTransaction(): Transaction {
    return Transaction(
        id = this.id,
        categoryId = this.categoryId,
        money = this.money,
        dateTime = dateTimeFormat.parse(this.dateTime)!!
    )
}
