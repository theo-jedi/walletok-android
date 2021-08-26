package com.theost.walletok.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionPatchDto(
    @SerialName("id") val transactionId: Int,
    @SerialName("walletId") val walletId: Int,
    @SerialName("categoryId") val categoryId: Int,
    @SerialName("amount") val amount: Long,
    @SerialName("date") val date: String
)