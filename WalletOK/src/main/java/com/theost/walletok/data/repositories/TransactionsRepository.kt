package com.theost.walletok.data.repositories

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.TransactionContentDto
import com.theost.walletok.data.dto.TransactionsDto
import com.theost.walletok.data.dto.mapToTransactionsAndNextId
import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.Transaction
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

object TransactionsRepository {
    const val LIMIT = 20
    private val service = WalletOkService.getInstance()
    private val transactions = (0..9).map {
        Transaction(
            id = it,
            categoryId = 0,
            money = 12000,
            currency = Currency.RUB,
            dateTime = TransactionsDto.dateTimeFormat
                .parse("2021-08-${12 + it}T12:08:56Z+0300")!!
        )
    }.toMutableList()

    fun getNextTransactions(
        walletId: Int,
        nextTransactionId: Int?
    ): Single<Pair<List<Transaction>, Int?>> {
        if (nextTransactionId == null)
            return if (transactions.isNotEmpty()) getTransactionsFromCache()
            else getTransactionsFromServer(walletId, nextTransactionId).doOnSuccess {
                transactions.addAll(it.first)
            }
        return getTransactionsFromServer(walletId, nextTransactionId).doOnSuccess { pair ->
            val nextTransactionInCache = transactions.find { it.id == nextTransactionId }
            if (nextTransactionInCache != null) {
                transactions.subList(
                    transactions.indexOf(nextTransactionInCache),
                    transactions.size - 1
                ).clear()
            }
            transactions.addAll(pair.first)
        }
    }

    private fun getTransactionsFromServer(walletId: Int, nextTransactionId: Int?):
            Single<Pair<List<Transaction>, Int?>> {
        return service
            .getTransactions(walletId, LIMIT, nextTransactionId)
            .map { it.mapToTransactionsAndNextId() }
    }

    private fun getTransactionsFromCache(): Single<Pair<List<Transaction>, Int?>> {
        return Single.just(
            Pair(transactions, transactions.last().id)
        )
    }

    fun addTransaction(value: String, category: Int): Completable {
        return Completable.fromAction {
            val transaction = simulateServerResponse(value, category)
            transactions.add(transaction)
        }
    }

    private fun simulateServerResponse(value: String, category: Int): Transaction {
        return Transaction(
            transactions.size + 1,
            category,
            value.toInt() * 100,
            Currency.RUB,
            Calendar.getInstance().time
        )
    }

    fun removeTransaction(walletId: Int, id: Int): Completable {
        return Completable.fromAction {
            transactions.removeAll { it.id == id }
        }
    }

    fun addTransaction(walletId: Int, dto: TransactionContentDto): Completable {
        return addTransaction(value = dto.money.toString(), category = dto.categoryId)
    }
}