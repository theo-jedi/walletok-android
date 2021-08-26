package com.theost.walletok.data.repositories

import android.accounts.NetworkErrorException
import com.theost.walletok.App
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.db.entities.mapToEntity
import com.theost.walletok.data.db.entities.mapToTransaction
import com.theost.walletok.data.dto.TransactionPostDto
import com.theost.walletok.data.dto.mapToTransaction
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCategoryType
import com.theost.walletok.data.models.TransactionsAndLastId
import com.theost.walletok.utils.RxResource
import com.theost.walletok.utils.Status
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.math.abs

object TransactionsRepository {
    const val LIMIT = 20
    private val service = WalletOkService.getInstance()
    private val transactions = mutableMapOf<Int, MutableList<Transaction>>()

    fun getTransactionsFromServer(walletId: Int, lastTransactionId: Int?):
            Single<RxResource<TransactionsAndLastId>> {
        return service
            .getTransactions(walletId, LIMIT, lastTransactionId)
            .subscribeOn(Schedulers.io())
            .map { list ->
                TransactionsAndLastId(
                    list.map { it.mapToTransaction() },
                    if (list.size < LIMIT) null
                    else list.last().id
                )
            }
            .map { RxResource.success(it) }
            .onErrorReturn { RxResource.error(it, null) }
            .subscribeOn(Schedulers.io())
    }

    fun getTransactionsFromCache(walletId: Int): Single<RxResource<TransactionsAndLastId>> {
        if (transactions[walletId] == null) transactions[walletId] = mutableListOf()
        val currentTransactions: List<Transaction> = transactions[walletId]!!
        return if (currentTransactions.isNotEmpty()) {
            Single.just(
                TransactionsAndLastId(
                    currentTransactions,
                    currentTransactions.last().id
                )
            ).map { RxResource.success(it) }
        } else App.appDatabase.transactionsDao().getAllFromWallet(walletId)
            .map { list -> list.map { it.mapToTransaction() } }
            .map { TransactionsAndLastId(it, null) }
            .map { RxResource.success(it) }
            .subscribeOn(Schedulers.io())
    }

    fun getTransactions(
        walletId: Int,
        lastTransactionId: Int?
    ): Observable<RxResource<TransactionsAndLastId>> {
        return Single.concat(
            getTransactionsFromCache(walletId),
            getTransactionsFromServer(walletId, lastTransactionId)
                .doOnSuccess {
                    if (it.data != null)
                        if (lastTransactionId == null) {
                            overwriteCacheOrCreateNew(walletId, it.data.transactions)
                            addTransactionsToDb(it.data.transactions)
                        } else transactions[walletId]!!.addAll(it.data.transactions)
                }
        ).toObservable()
    }

    fun getNextTransactions(
        walletId: Int,
        lastTransactionId: Int?
    ): Single<RxResource<TransactionsAndLastId>> {
        return getTransactionsFromServer(walletId, lastTransactionId)
            .map {
                if (it.status == Status.SUCCESS) RxResource.success(
                    TransactionsAndLastId(
                        transactions[walletId]!!.plus(it.data!!.transactions),
                        it.data.lastTransactionId
                    )
                )
                else it
            }
    }

    fun addTransaction(walletId: Int, amount: Long, categoryId: Int, type: String): Completable {
        return Completable.fromSingle(
            service.addTransaction(TransactionPostDto(walletId, categoryId,
                if (type == TransactionCategoryType.INCOME.uiName) amount else -abs(amount)))
                .subscribeOn(Schedulers.io())
                .doOnSuccess {
                    val transaction = it.mapToTransaction()
                    transactions[walletId]!!.add(transaction)
                    addTransactionsToDb(listOf(transaction))
                }
        )
    }

    fun removeTransaction(walletId: Int, id: Int): Completable {
        return service.deleteTransaction(id)
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                val transaction = transactions[walletId]!!.find { it.id == id }
                if (transaction != null) {
                    transactions[walletId]!!.removeAll { it.id == id }
                    removeTransactionFromDb(transaction)
                }
            }
    }

    fun editTransaction(id: Int, value: Long, category: Int, walletId: Int): Completable {

        return Completable.error(NetworkErrorException("Нет такого метода"))
    }

    private fun overwriteCacheOrCreateNew(walletId: Int, transactions: List<Transaction>) {
        TransactionsRepository.transactions[walletId]?.let {
            it.clear()
            it.addAll(transactions)
            App.appDatabase.transactionsDao().deleteAll(walletId)
                .subscribeOn(Schedulers.io())
                .subscribe()
        } ?: TransactionsRepository.transactions.put(walletId, transactions.toMutableList())
    }

    private fun addTransactionsToDb(transactions: List<Transaction>) {
        App.appDatabase.transactionsDao().insertAll(transactions.map {
            it.mapToEntity()
        })
            .subscribeOn(Schedulers.io()).subscribe()
    }

    private fun removeTransactionFromDb(transaction: Transaction) {
        App.appDatabase.transactionsDao().delete(transaction.mapToEntity())
            .subscribeOn(Schedulers.io()).subscribe()
    }
}