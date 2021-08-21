package com.theost.walletok.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TransactionCreationModel(
    var value: String? = null,
    var type: String? = null,
    var categoryId: Int? = null,
    var categoryName: String? = null
) : Parcelable {

    fun isFilled(): Boolean =
        (value != null && type != null && categoryId != null && categoryName != null)

}