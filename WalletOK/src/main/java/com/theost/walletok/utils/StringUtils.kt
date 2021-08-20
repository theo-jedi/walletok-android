package com.theost.walletok.utils

import java.text.DecimalFormat

object StringUtils {

    fun formatCurrency(input: String): String {
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
                format.format(formatNumber(numbers[0]).toDouble()).replace(",", " ")
            return formattedNumber + double
        }
        return ""
    }

    fun formatNumber(input: String) : String {
        return input.replace(" ", "")
    }

    fun currencyToDouble(input: String): Double {
        return if (input != "" && input != ".") {
            formatNumber(input).toDouble()
        } else {
            0.0
        }
    }

    fun isCurrencyValueValid(input: String): Boolean {
        return (currencyToDouble(input) != 0.0 && input[input.length - 1] != '.')
    }

}