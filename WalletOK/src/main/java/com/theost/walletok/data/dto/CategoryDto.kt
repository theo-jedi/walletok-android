package com.theost.walletok.data.dto

import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCategoryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("name") val name: String,
    @SerialName("id") val id: Int,
    @SerialName("iconColor") val iconColor: Int?,
    @SerialName("iconLink") val iconLink: String?,
    @SerialName("income") val income: Boolean,
    @SerialName("userId") val userId: Int?
)

fun CategoryDto.mapToCategory(): TransactionCategory {
    return TransactionCategory(
        id = this.id,
        iconColor = this.iconColor,
        iconLink = WalletOkService.BASE_URL+this.iconLink,
        name = this.name,
        type = if (this.income) TransactionCategoryType.INCOME else TransactionCategoryType.EXPENSE
    )
}