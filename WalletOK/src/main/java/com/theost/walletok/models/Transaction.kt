package com.theost.walletok.models

class Transaction(var value: String, var type: String, var category: String, var date: String) {

    fun isFilled() : Boolean = (value != "" && type != "" && category != "")

}