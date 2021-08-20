package com.theost.walletok.data

data class WalletInfo(
    val name: String,
    val currency: String,
    val amountOfMoney: String,
    val gain: String,
    val lose: String,
    val loseLimit: String
)