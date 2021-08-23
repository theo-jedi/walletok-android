package com.theost.walletok.utils

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val data: T?, val error: Throwable) : Resource<T>()
    class Loading<T>(val data: T?) : Resource<T>()
}