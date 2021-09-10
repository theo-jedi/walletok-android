package com.theost.walletok.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class CategoryCreationModel(
    var name: String? = null,
    var type: String? = null,
    var color: Int? = null,
    var iconUrl: Int? = null,
) : Parcelable {

    fun isFilled(): Boolean =
        (name != null && type != null && color != null && iconUrl != null)

}