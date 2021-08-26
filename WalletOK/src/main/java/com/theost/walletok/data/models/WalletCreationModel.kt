package com.theost.walletok.data.models

data class WalletCreationModel(
    var name: String = "",
    var currency: Currency? = null,
    var balanceLimit: Long? = null
)