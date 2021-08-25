package com.theost.walletok.presentation.wallet_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.data.repositories.TransactionsRepository
import com.theost.walletok.data.repositories.WalletsRepository
import com.theost.walletok.presentation.base.PaginationStatus
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.addTo
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

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
            TransactionsRepository.getTransactions(walletId, lastTransactionId),
            { categoriesResult, walletsResult, transactionsResult ->
                CategoriesWalletsTransactionsAndLastId(
                    categoriesResult,
                    walletsResult,
                    transactionsResult.transactions,
                    transactionsResult.lastTransactionId
                )
            }
        ).subscribe({
            _paginationStatus.postValue(
                if (it.lastTransactionId == null)
                    PaginationStatus.End
                else PaginationStatus.Ready
            )
            _allData.postValue(it)
        }, {
            _paginationStatus.postValue(PaginationStatus.Error)
        }).addTo(compositeDisposable)
    }

    fun loadNextPage() {
        _paginationStatus.postValue(PaginationStatus.Loading)
        TransactionsRepository.getTransactions(walletId, lastTransactionId)
            .subscribe({
                _paginationStatus.postValue(
                    if (it.lastTransactionId == null)
                        PaginationStatus.End
                    else PaginationStatus.Ready
                )
                val oldData = _allData.value
                if (oldData != null)
                    _allData.postValue(
                        CategoriesWalletsTransactionsAndLastId(
                            oldData.categories,
                            oldData.wallets,
                            it.transactions,
                            it.lastTransactionId
                        )
                    )
            }, {

            }).addTo(compositeDisposable)
    }

    fun removeTransaction(id: Int) {
        _removeTransactionStatus.postValue(Resource.Loading(Unit))
        TransactionsRepository.removeTransaction(walletId, id)
            .subscribe({
                _removeTransactionStatus.postValue(Resource.Success(Unit))
                loadData()
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

data class CategoriesWalletsTransactionsAndLastId(
    val categories: List<TransactionCategory>,
    val wallets: List<Wallet>,
    val transactions: List<Transaction>,
    val lastTransactionId: Int?
)