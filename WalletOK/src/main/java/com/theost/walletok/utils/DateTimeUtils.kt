package com.theost.walletok.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getFormattedDateOrCurrent(date: Date = Date()) : String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(date)
    }

    fun getCurrentDateTime() : String {
        return SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault()).format(Date()) // "hh:mm dd.MM.yyyy"
    }

}