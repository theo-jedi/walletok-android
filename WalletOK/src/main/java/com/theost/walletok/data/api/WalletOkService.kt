package com.theost.walletok.data.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.theost.walletok.data.dto.CategoryDto
import com.theost.walletok.data.dto.CurrencyDto
import com.theost.walletok.data.dto.TransactionContentDto
import com.theost.walletok.data.dto.WalletDto
import com.theost.walletok.utils.AuthUtils
import io.reactivex.Single
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WalletOkService {

    @GET("categories")
    fun getCategories(): Single<List<CategoryDto>>

    @GET("wallets")
    fun getWallets(): Single<List<WalletDto>>

    @GET("wallets/{id}/transactions")
    fun getTransactions(
        @Path("id") walletId: Int,
//        @Query("limit") limit: Int,
//        @Query("next_transaction_id") nextTransactionId: Int?
    ): Single<List<TransactionContentDto>>

    @GET("wallets/{id}/income")
    fun getWalletIncome(
        @Path("id") walletId: Int,
    ): Single<Long>

    @GET("wallets/{id}/expenditure")
    fun getWalletExpenditure(
        @Path("id") walletId: Int,
    ): Single<Long>

    @POST("wallets")
    fun addWallet(
        @Field("id") id: Int,
        @Field("currency") currency: CurrencyDto,
        @Field("balanceLimit") balanceLimit: Long
    ): Single<WalletDto>

    @POST("transactions")
    fun addTransaction(
        @Field("walletId") walletId: Int,
        @Field("categoryId") categoryId: Int,
        @Field("amount") amount: Long
    ): Single<TransactionContentDto>

    companion object {
        private const val BASE_URL = "http://34.88.199.231:9090/"
        private var instance: WalletOkService? = null

        fun getInstance(): WalletOkService {
            if (instance == null)
                instance = create()
            return instance as WalletOkService
        }

        private fun create(): WalletOkService {
            val logger =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            val authorization = Interceptor { chain ->
                val builder = chain.request().newBuilder()
                val account = AuthUtils.getLastSignedInAccount()
                builder.addHeader("accept", "*/*")
                account?.email?.let {
                    builder.addHeader("X-Email", it)
                }
                builder.addHeader("Content-Type", "application/json")
                chain.proceed(builder.build())
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(authorization)
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