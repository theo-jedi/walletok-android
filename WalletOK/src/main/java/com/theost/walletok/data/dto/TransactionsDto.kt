package com.theost.walletok.data.dto

import com.theost.walletok.data.dto.TransactionsDto.Companion.dateTimeFormat
import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionsAndNextId
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
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'Z", Locale("ru", "RU"))
    }
}

@Serializable
data class TransactionContentDto(
    @SerialName("id") val id: Int,
    @SerialName("category_id") val categoryId: Int,
    @SerialName("money") val money: Int,
    @SerialName("currency") val currency: Currency,
    @SerialName("date_time") val dateTime: String
)

fun TransactionsDto.mapToTransactionsAndNextId(): TransactionsAndNextId {
    val newList = this.transactions.map {
        Transaction(
            id = it.id,
            categoryId = it.categoryId,
            money = it.money,
            currency = it.currency,
            dateTime = dateTimeFormat.parse(it.dateTime)!!
        )
    }
    return TransactionsAndNextId(newList, this.nextTransactionId)
}
