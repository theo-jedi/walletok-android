package com.theost.walletok.widgets

interface TransactionListener {

    fun onSetTransactionData(data: String, key: String)

    fun onEditTransactionData(key: String)

    fun onCreateTransaction()

}