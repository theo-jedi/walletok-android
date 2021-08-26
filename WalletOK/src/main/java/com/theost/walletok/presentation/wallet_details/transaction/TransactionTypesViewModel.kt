package com.theost.walletok.presentation.wallet_details.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.data.models.TransactionCategoryType
import com.theost.walletok.delegates.TypeItem
import com.theost.walletok.utils.TypeModelUtils

class TransactionTypesViewModel : ViewModel() {

    private val _allData = MutableLiveData<List<TypeItem>>()
    val allData : LiveData<List<TypeItem>> = _allData

    fun loadData(savedType: String?) {
        _allData.value = TransactionCategoryType.values().map { type ->
            TypeItem(
                name = type.uiName,
                isSelected = savedType == type.uiName
            )
        }
    }

    fun selectData(position: Int) {
        _allData.value = TypeModelUtils.selectData(_allData.value!!, position)
    }

}