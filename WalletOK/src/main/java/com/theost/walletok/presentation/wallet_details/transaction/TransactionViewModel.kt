package com.theost.walletok.presentation.wallet_details.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.data.repositories.TransactionsRepository
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class TransactionViewModel : ViewModel() {

    private val _allData = MutableLiveData<TransactionCreationModel>()
    val allData : LiveData<TransactionCreationModel> = _allData
    private val compositeDisposable = CompositeDisposable()

    private val _loadingStatus = MutableLiveData<Resource<*>>()
    val loadingStatus: LiveData<Resource<*>> = _loadingStatus

    private val _sendingStatus = MutableLiveData<Resource<*>>()
    val sendingStatus: LiveData<Resource<*>> = _sendingStatus

    fun loadData(savedTransaction: Transaction) {
        _loadingStatus.postValue(Resource.Loading(Unit))
        CategoriesRepository.getCategories().subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val savedCategory = list.find { it.id == savedTransaction.categoryId }!!
                val transactionCreationModel = TransactionCreationModel()
                transactionCreationModel.id = savedTransaction.id
                transactionCreationModel.value = savedTransaction.money
                transactionCreationModel.type = savedCategory.type.uiName
                transactionCreationModel.category = savedTransaction.categoryId
                transactionCreationModel.currency = savedTransaction.currency
                transactionCreationModel.dateTime = savedTransaction.dateTime
                _allData.postValue(transactionCreationModel)
                _loadingStatus.postValue(Resource.Success(Unit))
            }, {
                _loadingStatus.postValue(Resource.Error(Unit, it))
            }).addTo(compositeDisposable)
    }

    fun sendData(transactionModel: TransactionCreationModel, walletId: Int) {
        _sendingStatus.postValue(Resource.Loading(Unit))
        if (transactionModel.id != null) {
            TransactionsRepository.editTransaction(
                transactionModel.id!!,
                transactionModel.value!!,
                transactionModel.category!!,
                transactionModel.dateTime!!,
                walletId
            ).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _sendingStatus.postValue(Resource.Success(Unit))
                }, {
                    _sendingStatus.postValue(Resource.Error(Unit, it))
                }).addTo(compositeDisposable)
        } else {
            TransactionsRepository.addTransaction(
                walletId,
                transactionModel.value!!,
                transactionModel.category!!,
                transactionModel.dateTime!!
            ).subscribeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _sendingStatus.postValue(Resource.Success(Unit))
                }, {
                    _sendingStatus.postValue(Resource.Error(Unit, it))
                }).addTo(compositeDisposable)
        }
    }

    fun setValue(value: Long) {
        _allData.value?.value = value
    }

    fun setType(type: String) {
        _allData.value?.type = type
    }

    fun setCategory(category: Int) {
        _allData.value?.category = category
    }

    fun setDateTime(dateTime: Date) {
        _allData.value?.dateTime = dateTime
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}