package com.theost.walletok.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.theost.walletok.data.db.dao.CategoriesDao
import com.theost.walletok.data.db.dao.CurrenciesDao
import com.theost.walletok.data.db.dao.TransactionsDao
import com.theost.walletok.data.db.dao.WalletsDao
import com.theost.walletok.data.db.entities.CategoryEntity
import com.theost.walletok.data.db.entities.CurrencyEntity
import com.theost.walletok.data.db.entities.TransactionEntity
import com.theost.walletok.data.db.entities.WalletEntity

@Database(
    entities = [
        CurrencyEntity::class,
        TransactionEntity::class,
        CategoryEntity::class,
        WalletEntity::class],
    version = 1
)
abstract class WalletOKDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoriesDao
    abstract fun currenciesDao(): CurrenciesDao
    abstract fun transactionsDao(): TransactionsDao
    abstract fun walletsDao(): WalletsDao
}