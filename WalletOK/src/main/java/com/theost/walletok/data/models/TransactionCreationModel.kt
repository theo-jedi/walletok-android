package com.theost.walletok.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TransactionCreationModel(
    var id: Int? = null,
    var value: Int? = null,
    var type: String? = null,
    var category: Int? = null,
    var currency: String? = null,
    var dateTime: String? = null,
) : Parcelable {

    fun isFilled(): Boolean =
        (value != null && type != null && category != null)

}