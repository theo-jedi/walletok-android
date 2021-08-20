package com.theost.walletok.data.repositories

import com.theost.walletok.data.Transaction
import com.theost.walletok.utils.DateTimeUtils

object TransactionsRepository {
    private val transactions = mutableListOf<Transaction>()

    fun getTransactions(): List<Transaction> = transactions

    fun addTransaction(value: String, category: Int) {
        val transaction = simulateServerResponse(value, category)
        transactions.add(transaction)
    }

    private fun simulateServerResponse(value: String, category: Int) : Transaction {
        return Transaction(transactions.size + 1, category, value, "₽", DateTimeUtils.getCurrentDateTime())
    }

    fun removeTransaction(id: Int) {
        transactions.removeAll { it.id == id }
    }
}