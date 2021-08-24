package com.theost.walletok.presentation.wallets

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.data.models.Wallet
import com.theost.walletok.data.models.WalletsOverall
import com.theost.walletok.data.repositories.WalletsRepository
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.addTo
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class WalletsViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()
    private val _loadingStatus = MutableLiveData<Resource<*>>()
    val loadingStatus: LiveData<Resource<*>> = _loadingStatus
    private val _walletsAndOverall = MutableLiveData<WalletsAndOverall>()
    val walletsAndOverall: LiveData<WalletsAndOverall> = _walletsAndOverall
    fun loadData() {
        _loadingStatus.postValue(Resource.Loading(Unit))
        Single.zip(
            WalletsRepository.getWallets(),
            WalletsRepository.getWalletsOverall(),
            { wallets, walletsOverall ->
                WalletsAndOverall(wallets, walletsOverall)
            })
            .subscribeOn(Schedulers.io())
            .subscribe({
                _loadingStatus.postValue(Resource.Success(Unit))
                _walletsAndOverall.postValue(it)
            }, {
                _loadingStatus.postValue(Resource.Error(Unit, it))
            }).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

data class WalletsAndOverall(val wallets: List<Wallet>, val walletsOverall: WalletsOverall)