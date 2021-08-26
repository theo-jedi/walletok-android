package com.theost.walletok.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.walletok.data.db.entities.CurrencyEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface CurrenciesDao {
    @Query("SELECT * FROM currencies")
    fun getAll(): Single<List<CurrencyEntity>>

    @Query("SELECT * FROM currencies WHERE short_name LIKE :shortName LIMIT 1")
    fun findByName(shortName: String): Single<CurrencyEntity>

    @Insert(onConflict = REPLACE)
    fun insertAll(currencies: List<CurrencyEntity>): Completable

    @Delete
    fun delete(currency: CurrencyEntity): Completable

    @Query("DELETE FROM currencies")
    fun deleteAll(): Completable
}