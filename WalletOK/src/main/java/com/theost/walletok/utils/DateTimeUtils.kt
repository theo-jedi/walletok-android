package com.theost.walletok.utils

import java.text.SimpleDateFormat
import java.util.*

object DateTimeUtils {

    fun getCurrentDate() : String {
        return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
    }

}