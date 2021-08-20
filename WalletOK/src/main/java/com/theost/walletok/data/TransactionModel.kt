package com.theost.walletok.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TransactionModel(var value: String?, var type: String?, var category: Int?) : Parcelable {

    constructor() : this(null, null, null)

    fun isFilled() : Boolean = (value != null && type != null && category != null)

}