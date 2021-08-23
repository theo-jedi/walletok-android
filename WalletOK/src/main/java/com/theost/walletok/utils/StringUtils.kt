package com.theost.walletok.utils

import java.text.DecimalFormat

object StringUtils {

    fun formatMoney(input: String): String {
        if (input != "") {
            var value = input
            if (value[0] == '.') value = "0$input"
            var double = ""
            val numbers = value.split(".")
            if (numbers.size == 2) {
                double = "." + numbers[1]
                if (double.length > 3) double = double.substring(0, 3)
            }
            val format = DecimalFormat("###,###")
            val formattedNumber =
                format.format(replaceSpaces(numbers[0]).toDouble()).replace(",", " ")
            return formattedNumber + double
        }
        return ""
    }

    fun convertMoneyForStorage(value: String) : Int {
        return (replaceSpaces(value).toDouble() * 100).toInt()
    }

    fun convertMoneyForDisplay(value: Int) : String {
        return if (value % 100 != 0) {
            (value.toDouble() / 100).toString()
        } else {
            (value / 100).toString()
        }
    }

    fun isMoneyValueValid(input: String): Boolean {
        return if (input != "" && input != "." && input[input.length - 1] != '.') {
            replaceSpaces(input).toDouble() != 0.0
        } else {
            false
        }
    }

    private fun replaceSpaces(input: String) : String {
        return input.replace(" ", "")
    }

}