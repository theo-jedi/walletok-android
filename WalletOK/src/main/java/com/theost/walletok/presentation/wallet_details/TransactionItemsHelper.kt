package com.theost.walletok.presentation.wallet_details

import android.graphics.Color
import com.theost.walletok.R
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCategoryType
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.presentation.base.PaginationStatus
import com.theost.walletok.presentation.wallet_details.delegates.DateContent
import com.theost.walletok.presentation.wallet_details.delegates.HeaderContent
import com.theost.walletok.presentation.wallet_details.delegates.TransactionContent
import com.theost.walletok.utils.DateTimeUtils
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
                walletLoseLimit = wallet.loseLimit?.let
                { "${StringUtils.formatMoney((wallet.loseLimit/100).toString())} ${wallet.currency.symbol}" },
                walletLose = "${StringUtils.formatMoney((wallet.lose/100).toString())} ${wallet.currency.symbol}",
                walletGain = "${StringUtils.formatMoney((wallet.gain/100).toString())} ${wallet.currency.symbol}",
                walletMoney = "${StringUtils.formatMoney((wallet.amountOfMoney/100).toString())} ${wallet.currency.symbol}",
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
                var category = categories.find { category -> category.id == transaction.categoryId }
                if (category == null) category = TransactionCategory(
                    id = -1,
                    iconColor = -4278091,
                    iconLink = R.drawable.ic_category_deleted,
                    type = TransactionCategoryType.EXPENSE,
                    name = "Категория удалена",
                    userId = null
                )
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
                        } ${wallet.currency.symbol}",
                        time = DateTimeUtils.getFormattedTime(transaction.dateTime),
                        iconColor = category.iconColor,
                        iconUrl = category.iconLink
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