package com.theost.walletok.data.models

data class Wallet(
    val id: Int,
    val name: String,
    val currency: Currency,
    val amountOfMoney: Long,
    val gain: Long,
    val lose: Long,
    val loseLimit: Long?,
    val hidden: Boolean
)