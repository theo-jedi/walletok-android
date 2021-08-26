package com.theost.walletok.data.dto

import com.theost.walletok.data.models.Currency
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CurrencyDto(
    @SerialName("shortName") val shortName: String,
    @SerialName("decimalDigits") val decimalDigits: Int
)


fun CurrencyDto.mapToCurrency(): Currency {
    return Currency(
        shortName = shortName,
        decimalDigits = decimalDigits
    )
}