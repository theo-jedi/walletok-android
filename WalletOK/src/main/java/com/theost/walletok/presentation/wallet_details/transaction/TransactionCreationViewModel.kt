package com.theost.walletok.presentation.wallet_details.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.App
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.addTo
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class TransactionCreationViewModel : ViewModel() {

    private val _allData = MutableLiveData<Pair<TransactionCreationModel, String>>()
    val allData : LiveData<Pair<TransactionCreationModel, String>> = _allData

    private val _loadingStatus = MutableLiveData<Resource<*>>()
    val loadingStatus: LiveData<Resource<*>> = _loadingStatus

    private val compositeDisposable = CompositeDisposable()
    private lateinit var categoryName: String

    fun loadData(transactionCreationModel: TransactionCreationModel) {
        _loadingStatus.postValue(Resource.Loading(Unit))
        App.appDatabase.categoriesDao().getAll().subscribeOn(Schedulers.io())
            .subscribe({ list ->
                val category = list.find { item -> item.id == transactionCreationModel.category }
                categoryName = category?.name ?: "Категория удалена"
                _allData.postValue(Pair(transactionCreationModel, categoryName))
                _loadingStatus.postValue(Resource.Success(Unit))
            }, {
                _loadingStatus.postValue(Resource.Error(Unit, it))
            }).addTo(compositeDisposable)
    }

    fun setDate(dateTime: Date) {
        val transaction = _allData.value?.first
        transaction!!.dateTime = dateTime
        _allData.value = Pair(transaction, categoryName)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}