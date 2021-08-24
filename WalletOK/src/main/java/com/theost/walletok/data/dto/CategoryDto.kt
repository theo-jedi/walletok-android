package com.theost.walletok.data.dto

import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCategoryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("name") val name: String,
    @SerialName("id") val id: Int,
    @SerialName("iconColor") val iconColor: Int,
    @SerialName("iconLink") val iconLink: String,
    @SerialName("type") val type: TransactionCategoryType
)

fun CategoryDto.mapToCategory(): TransactionCategory {
    return TransactionCategory(
        id = this.id,
        iconColor = this.iconColor,
        iconLink = this.iconLink,
        name = this.name,
        type = this.type
    )
}