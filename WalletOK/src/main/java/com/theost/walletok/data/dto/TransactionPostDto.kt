package com.theost.walletok.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class TransactionPostDto(
    @SerialName("walletId") val walletId: Int,
    @SerialName("categoryId") val categoryId: Int,
    @SerialName("amount") val amount: Long
)