package com.theost.walletok.utils

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val data: T?, val error: Throwable) : Resource<T>()
    class Loading<T>(val data: T?) : Resource<T>()
}

data class RxResource<out T>(val status: Status, val data: T?, val error: Throwable?) {
    companion object {
        fun <T> success(data: T?): RxResource<T> {
            return RxResource(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Throwable, data: T?): RxResource<T> {
            return RxResource(Status.ERROR, data, error)
        }

        fun <T> loading(data: T?): RxResource<T> {
            return RxResource(Status.LOADING, data, null)
        }
    }
}

enum class Status {
    SUCCESS, LOADING, ERROR
}