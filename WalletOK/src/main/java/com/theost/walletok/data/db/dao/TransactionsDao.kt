package com.theost.walletok.data.db.dao

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.theost.walletok.data.db.entities.TransactionEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TransactionsDao {
    @Query("SELECT * FROM transactions")
    fun getAll(): Single<List<TransactionEntity>>

    @Insert(onConflict = REPLACE)
    fun insertAll(transactions: List<TransactionEntity>): Completable

    @Delete
    fun delete(transaction: TransactionEntity): Completable
}