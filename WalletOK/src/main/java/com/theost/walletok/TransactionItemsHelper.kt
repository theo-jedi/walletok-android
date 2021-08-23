package com.theost.walletok

import com.theost.walletok.base.PaginationStatus
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.delegates.DateContent
import com.theost.walletok.delegates.HeaderContent
import com.theost.walletok.delegates.TransactionContent
import com.theost.walletok.utils.StringUtils
import java.text.SimpleDateFormat
import java.util.*


object TransactionItemsHelper {

    fun getRecyclerItems(
        categories: List<TransactionCategory>,
        transactions: List<Transaction>,
        wallet: Wallet,
        paginationStatus: PaginationStatus?
    ): List<Any> {
        val result = mutableListOf<Any>()
        result.add(
            HeaderContent(
                walletLoseLimit = "${wallet.loseLimit / 100} ₽",
                walletLose = "${wallet.lose / 100} ₽",
                walletGain = "${wallet.gain / 100} ₽",
                walletMoney = "${wallet.amountOfMoney / 100} ₽",
                walletName = wallet.name
            )
        )
        val currentLocale = Locale("ru", "RU")
        val dayMonthFormat = SimpleDateFormat("dd MMMM", currentLocale)
        val dayMonthYearFormat = SimpleDateFormat("yyyy/MM/dd", currentLocale)
        val timeFormat = SimpleDateFormat("hh:mm", currentLocale)

        val calendar = Calendar.getInstance()
        val today = dayMonthYearFormat.format(calendar.time)
        calendar.add(Calendar.DATE, -1)
        val yesterday = dayMonthYearFormat.format(calendar.time)

        transactions.sortedByDescending { it.dateTime }
            .fold("") { lastItemDate: String, transaction: Transaction ->
                val transactionDate =
                    dayMonthYearFormat.format(transaction.dateTime)
                if (transactionDate != lastItemDate) {
                    result.add(
                        when (transactionDate) {
                            today -> DateContent("Сегодня")
                            yesterday -> DateContent("Вчера")
                            else -> DateContent(
                                dayMonthFormat.format(transaction.dateTime)
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
                        moneyAmount = "${
                            StringUtils.formatMoney(
                                StringUtils.convertMoneyForDisplay(
                                    transaction.money
                                )
                            )
                        } ${transaction.currency.symbol}",
                        time = timeFormat.format(transaction.dateTime),
                        image = category.image
                    )
                )
                transactionDate
            }
        when (paginationStatus) {
            PaginationStatus.Loading, PaginationStatus.Error -> result.add(paginationStatus)
            else -> {
            }
        }
        return result
    }
}