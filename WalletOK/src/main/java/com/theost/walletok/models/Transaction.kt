package com.theost.walletok.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Transaction(var value: String, var type: String, var category: String, var date: String) : Parcelable {

    constructor() : this("", "", "", "")

    fun isFilled() : Boolean = (value != "" && type != "" && category != "")

}