package com.theost.walletok.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.walletok.data.db.entities.TransactionEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface TransactionsDao {
    @Query("SELECT * FROM transactions WHERE wallet_id LIKE :walletId")
    fun getAllFromWallet(walletId: Int): Single<List<TransactionEntity>>

    @Insert(onConflict = REPLACE)
    fun insertAll(transactions: List<TransactionEntity>): Completable

    @Delete
    fun delete(transaction: TransactionEntity): Completable

    @Query("DELETE FROM transactions WHERE wallet_id LIKE :walletId")
    fun deleteAll(walletId: Int): Completable
}