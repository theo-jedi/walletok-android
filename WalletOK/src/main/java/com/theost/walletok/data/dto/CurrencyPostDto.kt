package com.theost.walletok.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CurrencyPostDto(@SerialName("shortName") val shortName: String)