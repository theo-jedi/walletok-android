package com.theost.walletok.data.repositories

import com.theost.walletok.data.models.Transaction
import com.theost.walletok.utils.DateTimeUtils
import io.reactivex.Completable
import io.reactivex.Single

object TransactionsRepository {
    private val transactions = (0..9).map {
        Transaction(
            id = it,
            categoryId = 0,
            money = 12000,
            currency = "rub",
            dateTime = "2021/08/${11 + it} 18:00"
        )
    }.toMutableList()

    fun getTransactions(walletId: Int): Single<List<Transaction>> {
        return Single.just(transactions)
    }

    fun addTransaction(value: Int, category: Int): Completable {
        val transaction = simulateCreation(value, category)
        transactions.add(transaction)
        return Completable.complete()
    }

    fun editTransaction(id: Int, value: Int, category: Int): Completable {
        val transaction = simulateEditing(id, value, category)
        removeTransaction(id)
        transactions.add(transaction)
        return Completable.complete()
    }

    private fun simulateCreation(value: Int, category: Int): Transaction {
        return Transaction(
            transactions.size + 1, category, value, "₽", DateTimeUtils.getCurrentDateTime()
        )
    }

    private fun simulateEditing(id: Int, value: Int, category: Int): Transaction {
        var currency = ""
        var dateTime = ""
        transactions.forEach {
            if (it.id == id) {
                currency = it.currency
                dateTime = it.dateTime
                return@forEach
            }
        }
        return Transaction(
            id,
            category,
            value,
            currency,
            dateTime
        )
    }

    fun removeTransaction(id: Int): Completable {
        return Completable.fromAction {
            transactions.removeAll { it.id == id }
        }
    }
}