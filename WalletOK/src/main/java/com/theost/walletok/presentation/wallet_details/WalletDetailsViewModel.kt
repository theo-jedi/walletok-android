package com.theost.walletok.presentation.wallet_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.data.repositories.CurrenciesRepository
import com.theost.walletok.data.repositories.TransactionsRepository
import com.theost.walletok.data.repositories.WalletsRepository
import com.theost.walletok.presentation.base.PaginationStatus
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.RxResource
import com.theost.walletok.utils.Status
import com.theost.walletok.utils.addTo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WalletDetailsViewModel(private val walletId: Int) : ViewModel() {
    private var lastTransactionId: Int? = null
    private val compositeDisposable = CompositeDisposable()
    private val _paginationStatus = MutableLiveData(PaginationStatus.Ready)
    private val _removeTransactionStatus = MutableLiveData<Resource<Unit>>()
    private val _allData =
        MutableLiveData<CategoriesWalletsTransactionsAndLastId>()
    val removeTransactionStatus: LiveData<Resource<Unit>> = _removeTransactionStatus
    val allData: LiveData<CategoriesWalletsTransactionsAndLastId> =
        _allData
    val paginationStatus: LiveData<PaginationStatus> = _paginationStatus

    fun loadData() {
        _paginationStatus.postValue(PaginationStatus.Loading)
        lastTransactionId = null
        Observable.zip(
            CategoriesRepository.getCategories(),
            WalletsRepository.getWallets(),
            CurrenciesRepository.getCurrencies(),
            TransactionsRepository.getTransactions(walletId, lastTransactionId),
            { categoriesResult, walletsResult, _, transactionsResult ->
                val error = categoriesResult.error
                    ?: walletsResult.error
                    ?: transactionsResult.error
                if (error == null)
                    RxResource.success(
                        CategoriesWalletsTransactionsAndLastId(
                            categoriesResult.data!!,
                            walletsResult.data!!,
                            transactionsResult.data!!.transactions,
                            transactionsResult.data.lastTransactionId
                        )
                    )
                else RxResource.error(error, null)
            }
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.status == Status.SUCCESS) {
                    _paginationStatus.value =
                        if (it.data!!.lastTransactionId == null)
                            PaginationStatus.End
                        else {
                            lastTransactionId = it.data.lastTransactionId
                            PaginationStatus.Ready
                        }
                    _allData.value = it.data
                }
                if (it.status == Status.ERROR)
                    _paginationStatus.value = PaginationStatus.Error
            }, {
                _paginationStatus.value = PaginationStatus.Error
            }).addTo(compositeDisposable)
    }

    fun loadNextPage() {
        if (_allData.value == null || _allData.value!!.transactions.isNullOrEmpty()) {
            _paginationStatus.postValue(PaginationStatus.Loading)
            TransactionsRepository.getNextTransactions(walletId, lastTransactionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.status == Status.SUCCESS) {
                        _paginationStatus.value =
                            if (it.data!!.lastTransactionId == null)
                                PaginationStatus.End
                            else {
                                lastTransactionId = it.data.lastTransactionId
                                PaginationStatus.Ready
                            }
                        val oldData = _allData.value
                        if (oldData != null)
                            _allData.value =
                                CategoriesWalletsTransactionsAndLastId(
                                    oldData.categories,
                                    oldData.wallets,
                                    it.data.transactions,
                                    it.data.lastTransactionId
                                )
                    }
                    if (it.status == Status.ERROR)
                        _paginationStatus.postValue(PaginationStatus.Error)
                }, {
                    _paginationStatus.postValue(PaginationStatus.Error)
                }).addTo(compositeDisposable)
        }
    }

    fun removeTransaction(id: Int) {
        _removeTransactionStatus.value = Resource.Loading(Unit)
        TransactionsRepository.removeTransaction(walletId, id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _removeTransactionStatus.value = Resource.Success(Unit)
                loadData()
            }, {
                _removeTransactionStatus.value = Resource.Error(Unit, it)
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

data class CategoriesWalletsTransactionsAndLastId(
    val categories: List<TransactionCategory>,
    val wallets: List<Wallet>,
    val transactions: List<Transaction>,
    val lastTransactionId: Int?
)