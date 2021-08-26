package com.theost.walletok.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.Wallet

@Entity(tableName = "wallets")
class WalletEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "currency_short_name")
    val currencyShortName: String,
    @ColumnInfo(name = "income")
    val income: Long,
    @ColumnInfo(name = "expenditure")
    val expenditure: Long,
    @ColumnInfo(name = "balance_limit")
    val balanceLimit: Long?,
    @ColumnInfo(name = "hidden")
    val hidden: Boolean = false
)

fun WalletEntity.mapToWallet(currency: Currency): Wallet {
    return Wallet(
        id = this.id,
        amountOfMoney = this.income - this.expenditure,
        gain = this.income,
        lose = this.expenditure,
        loseLimit = this.balanceLimit,
        name = this.name,
        hidden = this.hidden,
        currency = currency
    )
}