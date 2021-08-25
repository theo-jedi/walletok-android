package com.theost.walletok.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class WalletPostDto(
    @SerialName("name") val name: String,
    @SerialName("currency") val currency: CurrencyPostDto,
    @SerialName("balanceLimit") val balanceLimit: Long?
)