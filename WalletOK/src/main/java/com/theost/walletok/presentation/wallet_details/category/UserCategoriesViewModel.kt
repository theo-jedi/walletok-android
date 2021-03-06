package com.theost.walletok.presentation.wallet_details.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.data.api.WalletOkService
import com.theost.walletok.data.dto.CategoryPostDto
import com.theost.walletok.data.models.TransactionCategoryType
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.delegates.CategoryItem
import com.theost.walletok.delegates.TypeItem
import com.theost.walletok.presentation.wallet_details.transaction.widgets.CategoryListener
import com.theost.walletok.utils.ModelUtils
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.ViewUtils
import com.theost.walletok.utils.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class UserCategoriesViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<CategoryItem>>()
    val allData: LiveData<List<CategoryItem>> = _allData
    private val compositeDisposable = CompositeDisposable()

    private val _loadingStatus = MutableLiveData<Resource<*>>()
    val loadingStatus: LiveData<Resource<*>> = _loadingStatus

    fun loadData() {
        _loadingStatus.postValue(Resource.Loading(Unit))
        CategoriesRepository.getCategories().subscribeOn(Schedulers.io())
            .subscribe({ list ->
                val categoryItems = list.data!!.filter { it.userId != null }.map { category ->
                    CategoryItem(
                        id = category.id,
                        name = category.name,
                        iconUrl = category.iconLink,
                        iconColor = category.iconColor,
                        isSelected = false
                    )
                }
                _allData.postValue(categoryItems)
                _loadingStatus.postValue(Resource.Success(Unit))
            }, {
                _loadingStatus.postValue(Resource.Error(Unit, it))
            }).addTo(compositeDisposable)
    }

    fun selectData(position: Int) {
        _allData.value = ModelUtils.selectCategoryData(_allData.value!!, position, false)
    }

    fun deleteSelectedData() {
        _loadingStatus.postValue(Resource.Loading(Unit))
        val category = _allData.value?.find { it.isSelected }
        CategoriesRepository.removeCategory(category!!).subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                _loadingStatus.postValue(Resource.Success(Unit))
            }, {
                _loadingStatus.postValue(Resource.Error(Unit, it))
            }).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

}