package com.theost.walletok.data.repositories

import android.accounts.NetworkErrorException
import com.theost.walletok.App
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.db.entities.TransactionEntity
import com.theost.walletok.data.db.entities.mapToTransaction
import com.theost.walletok.data.dto.TransactionPostDto
import com.theost.walletok.data.dto.TransactionsDto
import com.theost.walletok.data.dto.mapToTransaction
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionsAndLastId
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

object TransactionsRepository {
    const val LIMIT = 20
    private val service = WalletOkService.getInstance()
    private val transactions = mutableMapOf<Int, MutableList<Transaction>>()

    fun getTransactionsFromServer(walletId: Int, lastTransactionId: Int?):
            Single<TransactionsAndLastId> {
        return service
            .getTransactions(walletId)
            .subscribeOn(Schedulers.io())
            .map { list ->
                TransactionsAndLastId(
                    list.map { it.mapToTransaction() },
                    if (list.size < LIMIT) null else list.last().id
                )
            }
            .doOnSuccess {
                overwriteCacheOrCreateNew(walletId, it.transactions)
                if (lastTransactionId == null)
                    addTransactionsToDb(it.transactions)
            }
    }

    fun getTransactionsFromCache(walletId: Int): Single<TransactionsAndLastId> {
        if (transactions[walletId] == null) transactions[walletId] = mutableListOf()
        val currentTransactions: List<Transaction> = transactions[walletId]!!
        return if (currentTransactions.isNotEmpty()) {
            Single.just(
                TransactionsAndLastId(
                    currentTransactions,
                    currentTransactions.last().id
                )
            )
        } else App.appDatabase.transactionsDao().getAll()
            .map { list -> list.map { it.mapToTransaction() } }
            .map { TransactionsAndLastId(it, null) }
            .subscribeOn(Schedulers.io())
    }

    fun getTransactions(walletId: Int, lastTransactionId: Int?): Observable<TransactionsAndLastId> {
        return Observable.concat(
            getTransactionsFromCache(walletId).toObservable(),
            getTransactionsFromServer(walletId, lastTransactionId).toObservable()
        )
    }

    fun addTransaction(walletId: Int, amount: Long, categoryId: Int): Completable {
        return Completable.fromSingle(
            service.addTransaction(TransactionPostDto(walletId, categoryId, amount))
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    transactions[walletId]!!.add(it.mapToTransaction())
                }
        )
    }

    fun removeTransaction(walletId: Int, id: Int): Completable {
        return service.deleteTransaction(id)
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                transactions[walletId]!!.removeAll { it.id == id }
            }
    }

    fun editTransaction(id: Int, value: Long, category: Int, walletId: Int): Completable {
//        return Completable.fromAction {
//            val transaction = simulateEditing(id, value, category, walletId)
//            transactions[walletId]!!.removeAll { it.id == id }
//            addToCacheOrCreateNew(walletId, listOf(transaction))
//        }
        return Completable.error(NetworkErrorException("Нет такого метода"))
    }

    private fun overwriteCacheOrCreateNew(walletId: Int, transactions: List<Transaction>) {
        TransactionsRepository.transactions[walletId]?.let {
            it.clear()
            it.addAll(transactions)
        } ?: TransactionsRepository.transactions.put(walletId, transactions.toMutableList())

    }

    private fun addTransactionsToDb(transactions: List<Transaction>) {
        App.appDatabase.transactionsDao().insertAll(transactions.map {
            TransactionEntity(
                id = it.id,
                categoryId = it.categoryId,
                amount = it.money,
                walletId = it.walletId,
                date = TransactionsDto.dateTimeFormat.format(it.dateTime)
            )
        })
            .subscribeOn(Schedulers.io()).subscribe()
    }
}