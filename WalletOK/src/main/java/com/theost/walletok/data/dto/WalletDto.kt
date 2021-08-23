package com.theost.walletok.data.dto

import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.Wallet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WalletDto(
    @SerialName("name") val name: String,
    @SerialName("currency") val currency: Currency,
    @SerialName("id") val id: Int,
    @SerialName("money") val money: Int,
    @SerialName("gain") val gain: Int,
    @SerialName("lose") val lose: Int,
    @SerialName("lose_limit") val loseLimit: Int
)

fun WalletDto.mapToWallet(): Wallet {
    return Wallet(
        id = this.id,
        name = this.name,
        currency = this.currency,
        amountOfMoney = this.money,
        gain = this.gain,
        lose = this.lose,
        loseLimit = this.loseLimit
    )
}