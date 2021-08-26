package com.theost.walletok.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.walletok.data.db.entities.WalletEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface WalletsDao {
    @Query("SELECT * FROM wallets")
    fun getAll(): Single<List<WalletEntity>>

    @Insert(onConflict = REPLACE)
    fun insertAll(wallets: List<WalletEntity>): Completable

    @Delete
    fun delete(wallet: WalletEntity): Completable
}