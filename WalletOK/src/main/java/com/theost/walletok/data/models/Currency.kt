package com.theost.walletok.data.models

import android.os.Parcelable
import com.theost.walletok.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class Currency(
    val shortName: String,
    val decimalDigits: Int,
    val symbol: String = convertShortNameToSymbol(shortName),
    val longNameResId: Int = convertShortNameToLongNameResId(shortName)
) : Parcelable


private fun convertShortNameToSymbol(shortName: String) = when (shortName) {
    "RUB" -> "₽"
    "USD" -> "$"
    else -> shortName
}

private fun convertShortNameToLongNameResId(shortName: String) = when (shortName) {
    "RUB" -> R.string.russian_rouble
    "EUR" -> R.string.euro
    "USD" -> R.string.usd_name
    else -> R.string.currency
}