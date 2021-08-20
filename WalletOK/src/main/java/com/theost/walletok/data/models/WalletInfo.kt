package com.theost.walletok.data.models

data class WalletInfo(
    val name: String,
    val currency: String,
    val amountOfMoney: Int,
    val gain: Int,
    val lose: Int,
    val loseLimit: Int
)