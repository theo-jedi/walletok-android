package com.theost.walletok.widgets

interface TransactionListener {

    fun onTransactionSubmitted()

    fun onValueEdit()

    fun onTypeEdit()

    fun onCategoryEdit()

}