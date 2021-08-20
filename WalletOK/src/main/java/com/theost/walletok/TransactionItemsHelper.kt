package com.theost.walletok

import com.theost.walletok.data.Transaction
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.data.repositories.TransactionsRepository
import com.theost.walletok.data.repositories.WalletInfoRepository
import com.theost.walletok.delegates.DateContent
import com.theost.walletok.delegates.HeaderContent
import com.theost.walletok.delegates.TransactionContent
import io.reactivex.Single
import java.text.SimpleDateFormat
import java.util.*


object TransactionItemsHelper {

    fun getData(): Single<List<Any>> {
        val categoriesSingle = CategoriesRepository.getCategories()
        val transactionsSingle = TransactionsRepository.getTransactions()
        val walletInfoSingle = WalletInfoRepository.getWalletInfo()
        return Single.zip(
            categoriesSingle,
            transactionsSingle,
            walletInfoSingle,
            { categories, transactions, walletInfo ->
                val result = mutableListOf<Any>()
                result.add(
                    HeaderContent(
                        walletLoseLimit = "${walletInfo.loseLimit / 100} ₽",
                        walletLose = "${walletInfo.lose / 100} ₽",
                        walletGain = "${walletInfo.gain / 100} ₽",
                        walletMoney = "${walletInfo.amountOfMoney / 100} ₽",
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
                        val transactionUnformattedDate =
                            dateTimeFormat.parse(transaction.dateTime)!!
                        val transactionDate =
                            dayMonthYearFormat.format(transactionUnformattedDate)
                        if (transactionDate != lastItemDate) {
                            result.add(
                                when (transactionDate) {
                                    today -> DateContent("Сегодня")
                                    yesterday -> DateContent("Вчера")
                                    else -> DateContent(
                                        dayMonthFormat.format(transactionUnformattedDate)
                                    )
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
                                moneyAmount = "${transaction.money / 100} ₽",
                                time = timeFormat.format(transactionUnformattedDate),
                                image = category.image
                            )
                        )
                        transactionDate
                    }

                result
            }
        )
    }
}