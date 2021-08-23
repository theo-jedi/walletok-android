package com.theost.walletok.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
class TransactionCreationModel(
    var id: Int? = null,
    var value: Int? = null,
    var type: String? = null,
    var category: Int? = null,
    var currency: Currency? = null,
    var dateTime: Date? = null,
) : Parcelable {

    fun isFilled(): Boolean =
        (value != null && type != null && category != null)

}