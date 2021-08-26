package com.theost.walletok.presentation.wallet_details.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.delegates.CategoryItem
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class TransactionCategoryViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<CategoryItem>>()
    val allData : LiveData<List<CategoryItem>> = _allData
    private val compositeDisposable = CompositeDisposable()

    private val _loadingStatus = MutableLiveData<Resource<*>>()
    val loadingStatus: LiveData<Resource<*>> = _loadingStatus

    fun loadData(savedCategory: Int, savedType: String) {
        _loadingStatus.postValue(Resource.Loading(Unit))
        CategoriesRepository.getCategories().subscribeOn(Schedulers.io())
            .subscribe({ list ->
                val categoryItems = list.filter { category -> category.type.uiName == savedType }
                    .map { category ->
                    CategoryItem(
                        id = category.id,
                        name = category.name,
                        icon = category.image as Int,
                        isSelected = category.id == savedCategory
                    )
                }
                _allData.postValue(categoryItems)
                _loadingStatus.postValue(Resource.Success(Unit))
            }, {
                _loadingStatus.postValue(Resource.Error(Unit, it))
            }).addTo(compositeDisposable)
    }

    fun selectData(position: Int) {
        val categoryItems = mutableListOf<CategoryItem>()
        categoryItems.addAll(_allData.value!!)
        val isSelected = !categoryItems[position].isSelected
        categoryItems.forEach { it.isSelected = false }
        categoryItems[position].isSelected = isSelected
        _allData.value = categoryItems
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}