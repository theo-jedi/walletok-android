package com.theost.walletok.presentation.wallets.wallet_creation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.data.models.Currency
import com.theost.walletok.data.models.WalletCreationModel
import com.theost.walletok.data.repositories.CurrenciesRepository
import com.theost.walletok.data.repositories.WalletsRepository
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.addTo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WalletCreationViewModel : ViewModel() {
    var walletCreationModel = WalletCreationModel()
    private val _currencies = MutableLiveData<List<Currency>>()
    val currencies: LiveData<List<Currency>> = _currencies
    private val _addWalletStatus = MutableLiveData<Resource<*>>()
    private val _loadCurrenciesStatus = MutableLiveData<Resource<*>>()
    val loadCurrenciesStatus: LiveData<Resource<*>> = _loadCurrenciesStatus
    val addWalletStatus: LiveData<Resource<*>> = _addWalletStatus
    private val compositeDisposable = CompositeDisposable()

    fun addWallet() {
        _addWalletStatus.postValue(Resource.Loading(Unit))
        WalletsRepository.addWallet(walletCreationModel).subscribeOn(Schedulers.io())
            .subscribe({
                _addWalletStatus.postValue(Resource.Success(Unit))
            }, {
                _addWalletStatus.postValue(Resource.Error(Unit, it))
            }).addTo(compositeDisposable)
    }

    fun loadCurrencies() {
        _loadCurrenciesStatus.postValue(Resource.Loading(Unit))
        CurrenciesRepository.getCurrencies().subscribeOn(Schedulers.io()).subscribe({
            _currencies.postValue(it.data!!)
            _loadCurrenciesStatus.postValue(Resource.Success(Unit))
        }, {
            _loadCurrenciesStatus.postValue(Resource.Error(Unit, it))
        })
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}