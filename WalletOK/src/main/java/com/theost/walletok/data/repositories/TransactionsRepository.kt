package com.theost.walletok.data.repositories

import com.theost.walletok.data.Transaction

object TransactionsRepository {
    private val transactions = mutableListOf<Transaction>()

    fun getTransactions(): List<Transaction> = transactions
    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
    }

    fun removeTransaction(id: Int) {
        transactions.remove(transactions.find { it.id == id })
    }
}