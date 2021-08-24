package com.theost.walletok.presentation.wallet_details.transaction.widgets

interface TransactionListener {

    fun onTransactionSubmitted()

    fun onValueEdit()

    fun onTypeEdit()

    fun onCategoryEdit()

}