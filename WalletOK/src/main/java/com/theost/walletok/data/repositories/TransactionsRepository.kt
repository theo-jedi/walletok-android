package com.theost.walletok.data.repositories

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.mapToTransaction
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.data.models.TransactionsAndNextId
import io.reactivex.Completable
import io.reactivex.Single

object TransactionsRepository {
    const val LIMIT = 20
    private val service = WalletOkService.getInstance()
    private val transactions = mutableMapOf<Int, MutableList<Transaction>>()

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
            .getTransactions(walletId)
            .map { list -> TransactionsAndNextId(list.map { it.mapToTransaction() }, null) }
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

    fun addTransaction(walletId: Int, amount: Long, categoryId: Int): Completable {
        return Completable.fromSingle(
            service.addTransaction(walletId, categoryId, amount).doOnSuccess {
                transactions[walletId]!!.add(it.mapToTransaction())
            }
        )
    }

    fun removeTransaction(walletId: Int, id: Int): Completable {
//        return Completable.fromAction {
//            transactions[walletId]!!.removeAll { it.id == id }
//        }
        return Completable.complete() // TODO
    }

    fun editTransaction(id: Int, value: Long, category: Int, walletId: Int): Completable {
//        return Completable.fromAction {
//            val transaction = simulateEditing(id, value, category, walletId)
//            transactions[walletId]!!.removeAll { it.id == id }
//            addToCacheOrCreateNew(walletId, listOf(transaction))
//        }
        return Completable.complete() // TODO
    }

    fun addTransaction(
        walletId: Int,
        transactionCreationModel: TransactionCreationModel
    ): Completable {
        return addTransaction(
            walletId = walletId,
            amount = transactionCreationModel.value!!,
            categoryId = transactionCreationModel.category!!
        )
    }

}