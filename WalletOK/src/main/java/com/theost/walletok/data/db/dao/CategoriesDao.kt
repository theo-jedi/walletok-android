package com.theost.walletok.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.theost.walletok.data.db.entities.CategoryEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface CategoriesDao {
    @Query("SELECT * FROM categories")
    fun getAll(): Single<List<CategoryEntity>>

    @Insert(onConflict = REPLACE)
    fun insertAll(categories: List<CategoryEntity>): Completable

    @Delete
    fun delete(category: CategoryEntity): Completable

    @Query("DELETE FROM categories")
    fun deleteAll(): Completable
}