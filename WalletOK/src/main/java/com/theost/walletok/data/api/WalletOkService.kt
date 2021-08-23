package com.theost.walletok.data.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.theost.walletok.data.dto.CategoryDto
import com.theost.walletok.data.dto.TransactionsDto
import com.theost.walletok.data.dto.WalletDto
import io.reactivex.Single
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WalletOkService {

    @GET("categories")
    fun getCategories(): Single<List<CategoryDto>>

    @GET("wallets")
    fun getWallets(): Single<List<WalletDto>>

    @GET("wallets/{id}/transactions")
    fun getTransactions(
        @Path("id") walletId: Int,
        @Query("limit") limit: Int,
        @Query("next_transaction_id") nextTransactionId: Int?
    ): Single<TransactionsDto>

    companion object {
        private const val BASE_URL = "https://ya.ru/"
        private var instance: WalletOkService? = null

        fun getInstance(): WalletOkService {
            if (instance == null)
                instance = create()
            return instance as WalletOkService
        }

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
                .addConverterFactory(
                    @OptIn(ExperimentalSerializationApi::class)
                    Json.asConverterFactory(contentType)
                )
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(WalletOkService::class.java)
        }
    }
}