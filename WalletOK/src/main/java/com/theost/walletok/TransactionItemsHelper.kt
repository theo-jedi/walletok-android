package com.theost.walletok

import com.theost.walletok.data.Transaction
import com.theost.walletok.data.TransactionCategory
import com.theost.walletok.data.TransactionCategoryType
import com.theost.walletok.delegates.HeaderContent
import com.theost.walletok.delegates.TransactionContent
import java.text.SimpleDateFormat
import java.util.*


object TransactionItemsHelper {

    private val transactionCategories = mutableListOf(
        TransactionCategory(
            id = 0,
            image = R.drawable.ic_category_card,
            name = "Зарплата",
            type = TransactionCategoryType.INCOME
        )
    )
    private val data = mutableListOf<Any>()

    private fun populateData() {
        data.clear()
        data.addAll(
            listOf(
                HeaderContent(
                    walletName = "Кошелек 1",
                    walletMoney = "0 ₽",
                    walletGain = "0 ₽",
                    walletLose = "0 ₽",
                    walletLoseLimit = "15000 ₽"
                )
            ).plus(getTransactionItems())
        )
    }

    private val transactions = (0..9).map {
        Transaction(
            categoryId = 0,
            id = it,
            currency = "P",
            dateTime = "2021/08/1${it} 12:00",
            money = "100"
        )
    }.toMutableList()

    init {
        populateData()
    }

    private fun getTransactionItems(): List<Any> {
        val result = mutableListOf<Any>()
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
                            today -> "Сегодня"
                            yesterday -> "Вчера"
                            else -> dayMonthFormat.format(transactionDate)
                        }
                    )
                }
                val category =
                    transactionCategories.find { category -> category.id == transaction.categoryId }!!
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

    fun getData(): List<Any> = data

    fun deleteTransaction(position: Int) {
        val item = data[position]
        if (item is TransactionContent) {
            transactions.remove(transactions.find { it.id == item.transactionId })
            populateData()
        }
    }
}