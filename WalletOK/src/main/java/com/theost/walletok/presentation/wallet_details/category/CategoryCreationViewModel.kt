package com.theost.walletok.presentation.wallet_details.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.data.models.CategoryCreationModel

class CategoryCreationViewModel : ViewModel() {

    private val _allData = MutableLiveData<CategoryCreationModel>()
    val allData : LiveData<CategoryCreationModel> = _allData

    fun loadData(categoryCreationModel: CategoryCreationModel? = null) {
        if (categoryCreationModel == null) {
            _allData.value = CategoryCreationModel()
        } else {
            _allData.value = categoryCreationModel!!
        }
    }

    fun setColor(color: Int) {
        val category = _allData.value
        category?.color =  color
        _allData.value = category!!
    }

    fun setIcon(iconRes: Int) {
        val category = _allData.value
        category?.iconRes = iconRes
        _allData.value = category!!
    }

}