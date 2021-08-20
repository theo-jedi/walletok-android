package com.theost.walletok.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getCurrentDate() : String {
        return SimpleDateFormat("yyyy/MM/dd hh:mm", Locale.getDefault()).format(Date()) // "hh:mm dd.MM.yyyy"
    }

}