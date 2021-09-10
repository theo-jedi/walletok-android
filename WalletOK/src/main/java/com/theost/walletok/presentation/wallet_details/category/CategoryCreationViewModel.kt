package com.theost.walletok.presentation.wallet_details.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.App
import com.theost.walletok.data.models.CategoryCreationModel
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.addTo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CategoryCreationViewModel : ViewModel() {

    private val _allData = MutableLiveData<Pair<CategoryCreationModel, List<Int>>>()
    val allData : LiveData<Pair<CategoryCreationModel, List<Int>>> = _allData
    private val compositeDisposable = CompositeDisposable()

    private val _loadingStatus = MutableLiveData<Resource<*>>()
    val loadingStatus: LiveData<Resource<*>> = _loadingStatus

    fun loadData(categoryCreationModel: CategoryCreationModel? = null) {
        _loadingStatus.postValue(Resource.Loading(Unit))
        App.appDatabase.categoriesDao().getAll().subscribeOn(Schedulers.io())
            .subscribe({ list ->
                val iconUrls = list.map { category -> category.iconLink }.toSet().toList()
                if (categoryCreationModel == null) {
                    val pair = Pair(CategoryCreationModel(), iconUrls)
                    _allData.postValue(pair)
                } else {
                    val pair = Pair(categoryCreationModel!!, iconUrls)
                    _allData.postValue(pair)
                }
                _loadingStatus.postValue(Resource.Success(Unit))
            }, {
                _loadingStatus.postValue(Resource.Error(Unit, it))
            }).addTo(compositeDisposable)
    }

    fun setColor(color: Int) {
        val category = _allData.value?.first
        category?.color = color
        _allData.value = Pair(category!!, _allData.value?.second!!)
    }

    fun setIcon(iconUrl: Int) {
        val category = _allData.value?.first
        category?.iconUrl = iconUrl
        _allData.value = Pair(category!!, _allData.value?.second!!)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}