package com.theost.walletok.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getFormattedDateOrCurrent(date: Date = Date()) : String {
        return SimpleDateFormat("HH:mm - dd.MM.yyyy", Locale.getDefault()).format(date)
    }

    fun getFormattedTime(date: Date) : String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)
    }

    fun getFormattedForServer(date: Date = Date()) : String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(date)
    }

}