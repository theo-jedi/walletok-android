package com.theost.walletok.data.dto

import com.theost.walletok.data.models.Wallet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletDto(
    @SerialName("name") val name: String,
    @SerialName("currency") val currency: CurrencyDto,
    @SerialName("id") val id: Int,
    @SerialName("income") val income: Long,
    @SerialName("expenditure") val expenditure: Long,
    @SerialName("balanceLimit") val loseLimit: Long
)

fun WalletDto.mapToWallet(): Wallet {
    return Wallet(
        id = this.id,
        name = this.name,
        currency = this.currency.mapToCurrency(),
        amountOfMoney = this.income - this.expenditure,
        gain = this.income,
        lose = this.expenditure,
        loseLimit = this.loseLimit
    )
}