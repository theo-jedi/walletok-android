package com.theost.walletok

import com.theost.walletok.data.Transaction
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.data.repositories.TransactionsRepository
import com.theost.walletok.data.repositories.WalletInfoRepository
import com.theost.walletok.delegates.DateContent
import com.theost.walletok.delegates.HeaderContent
import com.theost.walletok.delegates.TransactionContent
import java.text.SimpleDateFormat
import java.util.*


object TransactionItemsHelper {

    fun getData(): List<Any> {
        val categories = CategoriesRepository.getCategories()
        val transactions = TransactionsRepository.getTransactions()
        val walletInfo = WalletInfoRepository.getWalletInfo()
        val result = mutableListOf<Any>()
        result.add(
            HeaderContent(
                walletLoseLimit = walletInfo.loseLimit,
                walletLose = walletInfo.lose,
                walletGain = walletInfo.gain,
                walletMoney = walletInfo.amountOfMoney,
                walletName = walletInfo.name
            )
        )
        val currentLocale = Locale("ru", "RU")

        val dateTimeFormat = SimpleDateFormat("yyyy/MM/dd hh:mm", currentLocale)
        val dayMonthFormat = SimpleDateFormat("dd MMMM", currentLocale)
        val dayMonthYearFormat = SimpleDateFormat("yyyy/MM/dd", currentLocale)
        val timeFormat = SimpleDateFormat("hh:mm", currentLocale)

        val calendar = Calendar.getInstance()
        val today = dayMonthYearFormat.format(calendar.time)
        calendar.add(Calendar.DATE, -1)
        val yesterday = dayMonthYearFormat.format(calendar.time)

        transactions.sortedByDescending { it.dateTime }
            .fold("") { lastItemDate: String, transaction: Transaction ->
                val transactionDate = dateTimeFormat.parse(transaction.dateTime)!!
                if (transaction.dateTime != lastItemDate) {
                    result.add(
                        when (dayMonthYearFormat.format(transactionDate)) {
                            today -> DateContent("Сегодня")
                            yesterday -> DateContent("Вчера")
                            else -> DateContent(dayMonthFormat.format(transactionDate))
                        }
                    )
                }
                val category =
                    categories.find { category -> category.id == transaction.categoryId }!!
                result.add(
                    TransactionContent(
                        transactionId = transaction.id,
                        categoryName = category.name,
                        transactionType = category.type.uiName,
                        moneyAmount = "${transaction.money} ${transaction.currency}",
                        time = timeFormat.format(transactionDate),
                        image = category.image
                    )
                )
                transaction.dateTime
            }

        return result
    }
}