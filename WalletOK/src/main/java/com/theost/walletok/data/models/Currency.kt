package com.theost.walletok.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(
    val shortName: String,
    val decimalDigits: Int,
    val symbol: String = convertShortNameToSymbol(shortName)
) : Parcelable


private fun convertShortNameToSymbol(shortName: String) = when (shortName) {
    "RUB" -> "₽"
    "USD" -> "$"
    else -> shortName
}