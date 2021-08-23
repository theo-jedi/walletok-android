package com.theost.walletok.data.dto

import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCategoryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("name") val name: String,
    @SerialName("id") val id: Int,
    @SerialName("image_url") val imageUrl: String,
    @SerialName("type") val type: TransactionCategoryType
)

fun CategoryDto.mapToCategory(): TransactionCategory {
    return TransactionCategory(
        id = this.id,
        image = this.imageUrl,
        name = this.name,
        type = this.type
    )
}