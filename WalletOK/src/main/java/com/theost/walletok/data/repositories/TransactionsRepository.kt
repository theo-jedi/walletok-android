package com.theost.walletok.data.repositories

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.TransactionsDto
import com.theost.walletok.data.dto.mapToTransactionsAndNextId
import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.data.models.TransactionsAndNextId
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

object TransactionsRepository {
    const val LIMIT = 20
    private val service = WalletOkService.getInstance()
    private val transactions = mutableMapOf(Pair(0, (0..9).map {
        Transaction(
            id = it,
            categoryId = 0,
            money = 12000,
            currency = Currency.RUB,
            dateTime = TransactionsDto.dateTimeFormat
                .parse("2021-08-${12 + it}T12:08:56Z+0300")!!
        )
    }.toMutableList()))

    fun getNextTransactions(
        walletId: Int,
        nextTransactionId: Int?
    ): Single<TransactionsAndNextId> {
        if (nextTransactionId == null)
            return if (transactions[walletId] != null) getTransactionsFromCache(walletId)
            else getTransactionsFromServer(walletId, nextTransactionId).doOnSuccess {
                addToCacheOrCreateNew(walletId, it.transactions)
            }
        return getTransactionsFromServer(walletId, nextTransactionId).doOnSuccess { entity ->
            val currentTransactions = transactions[walletId]
            if (currentTransactions != null) {
                val nextTransactionInCache =
                    currentTransactions.find { it.id == nextTransactionId }
                if (nextTransactionInCache != null) {
                    currentTransactions.subList(
                        currentTransactions.indexOf(nextTransactionInCache),
                        currentTransactions.size - 1
                    ).clear()
                }
                addToCacheOrCreateNew(walletId, entity.transactions)
            }
        }
    }

    private fun addToCacheOrCreateNew(walletId: Int, transactions: List<Transaction>) {
        TransactionsRepository.transactions[walletId]?.addAll(transactions)
            ?: TransactionsRepository.transactions.put(walletId, transactions.toMutableList())
    }

    private fun getTransactionsFromServer(walletId: Int, nextTransactionId: Int?):
            Single<TransactionsAndNextId> {
        return service
            .getTransactions(walletId, LIMIT, nextTransactionId)
            .map { it.mapToTransactionsAndNextId() }
    }

    private fun getTransactionsFromCache(walletId: Int): Single<TransactionsAndNextId> {
        val currentTransactions = transactions[walletId]
        return Single.just(
            currentTransactions?.let {
                TransactionsAndNextId(
                    currentTransactions,
                    currentTransactions.last().id
                )
            }
        )
    }

    fun addTransaction(walletId: Int, value: Int, category: Int): Completable {
        return Completable.fromAction {
            val transaction = simulateCreation(value, category)
            transactions[walletId]!!.add(transaction)
        }
    }

    private fun simulateCreation(value: Int, category: Int): Transaction {
        return Transaction(
            transactions.size + 1,
            category,
            value * 100,
            Currency.RUB,
            Calendar.getInstance().time
        )
    }

    private fun simulateEditing(id: Int, value: Int, category: Int, walletId: Int): Transaction {
        var currency: Currency
        var dateTime: Date
        transactions[walletId]!!.find { it.id == id }.apply {
            currency = this!!.currency
            dateTime = this.dateTime
        }
        return Transaction(
            id,
            category,
            value,
            currency,
            dateTime
        )
    }

    fun removeTransaction(walletId: Int, id: Int): Completable {
        return Completable.fromAction {
            transactions[walletId]!!.removeAll { it.id == id }
        }
    }

    fun editTransaction(id: Int, value: Int, category: Int, walletId: Int): Completable {
        return Completable.fromAction { //TODO
            val transaction = simulateEditing(id, value, category, walletId)
            transactions[walletId]!!.removeAll { it.id == id }
            addToCacheOrCreateNew(walletId, listOf(transaction))
        }
    }

    fun addTransaction(
        walletId: Int,
        transactionCreationModel: TransactionCreationModel
    ): Completable {
        return addTransaction(
            walletId = walletId,
            value = transactionCreationModel.value!!,
            category = transactionCreationModel.category!!
        )
    }

}