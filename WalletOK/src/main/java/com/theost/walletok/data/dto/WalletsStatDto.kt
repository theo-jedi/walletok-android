package com.theost.walletok.data.dto

import com.theost.walletok.data.models.WalletsOverall
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletsStatDto(
    @SerialName("name") val name: String,
    @SerialName("currency") val currencyDto: CurrencyDto,
    @SerialName("sum") val sum: Long,
    @SerialName("income") val income: Long,
    @SerialName("expenditure") val expenditure: Long
)

fun WalletsStatDto.mapToWalletsOverall(): WalletsOverall {
    return WalletsOverall(
        totalIncome = this.income,
        totalExpense = this.expenditure,
        currency = this.currencyDto.mapToCurrency()
    )
}