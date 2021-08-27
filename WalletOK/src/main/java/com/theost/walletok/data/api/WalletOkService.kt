package com.theost.walletok.data.api

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.theost.walletok.data.dto.*
import com.theost.walletok.utils.AuthUtils
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.http.*

interface WalletOkService {

    @GET("categories")
    fun getCategories(): Single<List<CategoryDto>>

    @GET("wallets")
    fun getWallets(): Single<List<WalletDto>>

    @GET("wallets/{id}")
    fun getWallet(
        @Path("id") id: Int
    ): Single<WalletDto>

    @PATCH("transactions/{id}")
    fun editTransaction(
        @Path("id") transactionId: Int,
        @Body body: TransactionPatchDto
    ): Completable

    @DELETE("categories/{id}")
    fun deleteCategory(
        @Path("id") id: Int
    ): Completable

    @POST("categories")
    fun addCategory(
        @Body body: CategoryPostDto
    ): Completable

    @GET("currencies")
    fun getCurrencies(): Single<List<CurrencyDto>>

    @GET("wallets/{id}/transactions")
    fun getTransactions(
        @Path("id") walletId: Int,
        @Query("limit") limit: Int = 10,
        @Query("lastId") lastTransactionId: Int?
    ): Single<List<TransactionContentDto>>

    @GET("wallets/{id}/income")
    fun getWalletIncome(
        @Path("id") walletId: Int,
    ): Single<Long>

    @GET("wallets/{id}/expenditure")
    fun getWalletExpenditure(
        @Path("id") walletId: Int,
    ): Single<Long>

    @GET("currencies/{input}_{output}")
    fun convertCurrency(
        @Path("input") inputShortName: String,
        @Path("output") outputShortName: String
    ): Single<Double>

    @GET("wallets/stat")
    fun getWalletsStat(): Single<WalletsStatDto>

    @POST("wallets")
    fun addWallet(
        @Body body: WalletPostDto
    ): Single<WalletDto>

    @DELETE("transactions/{id}")
    fun deleteTransaction(
        @Path("id") transactionId: Int
    ): Completable

    @POST("transactions")
    fun addTransaction(
        @Body body: TransactionPostDto
    ): Single<TransactionContentDto>

    companion object {
        const val BASE_URL = "http://34.88.199.231:9090/"
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
                account?.idToken?.let {
                    builder.addHeader("X-Token", it)
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