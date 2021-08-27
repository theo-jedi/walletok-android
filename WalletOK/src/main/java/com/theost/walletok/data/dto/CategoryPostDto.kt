package com.theost.walletok.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class CategoryPostDto(
    @SerialName("name")
    val name: String,
    @SerialName("iconColor")
    val iconColor: Int,
    @SerialName("iconLink")
    val iconLink: String,
    @SerialName("income")
    val income: Boolean
)