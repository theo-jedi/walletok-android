package com.theost.walletok.presentation.wallet_details.transaction.widgets

interface TransactionCategoryListener {

    fun onCategorySubmitted(category: Int)

    fun onCreateCategoryClicked()

    fun onDeleteCategoryClicked()

}