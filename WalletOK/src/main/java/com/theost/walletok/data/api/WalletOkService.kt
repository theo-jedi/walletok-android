package com.theost.walletok.data.api

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

interface WalletOkService {


    companion object {
        private const val BASE_URL = ""
        private var instance: WalletOkService? = null

        fun getInstance(): WalletOkService {
            if (instance == null)
                instance = create()
            return instance as WalletOkService
        }

        @OptIn(ExperimentalSerializationApi::class)
        private fun create(): WalletOkService {
            val logger =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            val contentType = "application/json".toMediaType()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(Json.asConverterFactory(contentType))
                .build()
                .create(WalletOkService::class.java)
        }
    }
}