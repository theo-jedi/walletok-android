package com.theost.walletok.widgets

interface TransactionListener {

    fun onSetValue(value: String)

    fun onSetType(type: String)

    fun onSetCategory(category: String)

    fun onEditValue()

    fun onEditType()

    fun onEditCategory()

    fun onCreateTransaction()

}