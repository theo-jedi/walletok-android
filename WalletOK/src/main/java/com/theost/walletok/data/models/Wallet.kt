package com.theost.walletok.data.models

data class Wallet(
    val id: Int,
    val name: String,
    val currency: Currency,
    val amountOfMoney: Int,
    val gain: Int,
    val lose: Int,
    val loseLimit: Int
)