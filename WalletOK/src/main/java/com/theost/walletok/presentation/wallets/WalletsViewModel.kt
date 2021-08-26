package com.theost.walletok.presentation.wallets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.models.WalletsOverall
import com.theost.walletok.data.repositories.CurrenciesRepository
import com.theost.walletok.data.repositories.WalletsRepository
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.RxResource
import com.theost.walletok.utils.Status
import com.theost.walletok.utils.addTo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WalletsViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val _loadingStatus = MutableLiveData<Resource<*>>()
    val loadingStatus: LiveData<Resource<*>> = _loadingStatus
    private val _walletsAndOverall = MutableLiveData<WalletsAndOverall>()
    val walletsAndOverall: LiveData<WalletsAndOverall> = _walletsAndOverall
    private val _currenciesPrices = MutableLiveData<List<Pair<String, Double>>>()
    val currenciesPrices: LiveData<List<Pair<String, Double>>> = _currenciesPrices
    fun loadData() {
        _loadingStatus.postValue(Resource.Loading(Unit))
        Observable.zip(
            CurrenciesRepository.getCurrencies(),
            WalletsRepository.getWallets(),
            WalletsRepository.getWalletsOverall(),
            { currencies, wallets, walletsOverall ->
                val error = currencies.error
                    ?: wallets.error
                    ?: walletsOverall.error
                if (error == null)
                    RxResource.success(WalletsAndOverall(wallets.data!!, walletsOverall.data!!))
                else RxResource.error(error, null)
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.status == Status.SUCCESS) {
                    _walletsAndOverall.value = it.data!!
                    _loadingStatus.value = Resource.Success(Unit)
                } else if (it.status == Status.ERROR)
                    _loadingStatus.value = Resource.Error(Unit, it.error!!)
            }, {
                _loadingStatus.value = Resource.Error(Unit, it)
            }).addTo(compositeDisposable)
        CurrenciesRepository.getCurrenciesPrices(listOf("USD", "EUR", "JPY"), "RUB")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.data != null)
                    _currenciesPrices.value = it.data
                if (it.status == Status.ERROR)
                    it.error!!.printStackTrace()
            }, {
            }).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

data class WalletsAndOverall(val wallets: List<Wallet>, val walletsOverall: WalletsOverall)