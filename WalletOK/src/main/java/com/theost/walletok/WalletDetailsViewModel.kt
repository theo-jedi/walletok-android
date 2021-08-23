package com.theost.walletok

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theost.walletok.base.PaginationStatus
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.data.repositories.TransactionsRepository
import com.theost.walletok.data.repositories.WalletsRepository
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.addTo
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WalletDetailsViewModel(private val walletId: Int) : ViewModel() {
    private var nextTransactionId: Int? = null
    private val compositeDisposable = CompositeDisposable()
    private val _paginationStatus = MutableLiveData(PaginationStatus.Ready)
    private val _removeTransactionStatus = MutableLiveData<Resource<Unit>>()
    private val _allData =
        MutableLiveData<Triple<List<TransactionCategory>, List<Transaction>, Wallet>>()
    val removeTransactionStatus: LiveData<Resource<Unit>> = _removeTransactionStatus
    val allData: LiveData<Triple<List<TransactionCategory>, List<Transaction>, Wallet>> =
        _allData
    val paginationStatus: LiveData<PaginationStatus> = _paginationStatus

    fun loadData() {
        val oldData = _allData.value
        _paginationStatus.postValue(PaginationStatus.Loading)
        Single.zip(
            CategoriesRepository.getCategories(),
            WalletsRepository.getWallets(), { categoriesResult, walletsResult ->
                Pair(categoriesResult, walletsResult)
            }
        )
            .subscribeOn(Schedulers.io())
            .subscribe({ pair ->
                val wallet = pair.second.find { it.id == walletId }
                if (wallet == null) {
                    _paginationStatus.postValue(PaginationStatus.Error)
                    return@subscribe
                }
                _allData.postValue(
                    Triple(pair.first, oldData?.second.orEmpty(), wallet)
                )
                loadTransactions(pair.first, wallet)
            }, {
                _paginationStatus.postValue(PaginationStatus.Error)
            }).addTo(compositeDisposable)
    }

    private fun loadTransactions(categories: List<TransactionCategory>, wallet: Wallet) {
        TransactionsRepository.getNextTransactions(walletId, nextTransactionId)
            .subscribeOn(Schedulers.io())
            .subscribe({
                val transactions = it.transactions
                nextTransactionId = if (it.nextTransactionId == null) {
                    _paginationStatus.postValue(PaginationStatus.End)
                    null
                } else {
                    _paginationStatus.postValue(PaginationStatus.Ready)
                    it.nextTransactionId
                }
                _allData.postValue(
                    Triple(categories, transactions, wallet)
                )
            }, {
                _paginationStatus.postValue(PaginationStatus.Error)
            }).addTo(compositeDisposable)
    }

    fun removeTransaction(id: Int) {
        _removeTransactionStatus.postValue(Resource.Loading(Unit))
        TransactionsRepository.removeTransaction(walletId, id)
            .subscribe({
                _removeTransactionStatus.postValue(Resource.Success(Unit))
            }, {
                _removeTransactionStatus.postValue(Resource.Error(Unit, it))
            }).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    class Factory(private val walletId: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return WalletDetailsViewModel(walletId) as T
        }
    }
}