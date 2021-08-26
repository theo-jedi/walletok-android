package com.theost.walletok.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.walletok.data.models.Currency

@Entity(tableName = "currencies")
class CurrencyEntity(
    @PrimaryKey
    @ColumnInfo(name = "short_name")
    val shortName: String,
    @ColumnInfo(name = "decimal_digits")
    val decimalDigits: Int
)

fun CurrencyEntity.mapToCurrency(): Currency {
    return Currency(
        shortName = this.shortName,
        decimalDigits = this.decimalDigits
    )
}