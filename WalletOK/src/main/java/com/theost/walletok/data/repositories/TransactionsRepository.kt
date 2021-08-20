package com.theost.walletok.data.repositories

import com.theost.walletok.data.Transaction
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

    fun getTransactions(): Single<List<Transaction>> {
        return Single.just(transactions)
    }

    fun addTransaction(value: String, category: Int): Completable {
        val transaction = simulateServerResponse(value, category)
        transactions.add(transaction)
        return Completable.complete()
    }

    private fun simulateServerResponse(value: String, category: Int): Transaction {
        return Transaction(
            transactions.size + 1, category, value, "₽", DateTimeUtils.getCurrentDateTime()
        )
    }

    fun removeTransaction(id: Int): Completable {
        transactions.removeAll { it.id == id }
        return Completable.complete()
    }
}