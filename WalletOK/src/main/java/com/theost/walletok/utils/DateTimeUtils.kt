package com.theost.walletok.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getFormattedDateOrCurrent(date: Date = Date()) : String {
        return SimpleDateFormat("HH:mm - dd.MM.yyyy", Locale.getDefault()).format(date)
    }

}