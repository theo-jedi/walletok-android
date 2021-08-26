package com.theost.walletok.presentation.wallet_details.transaction

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.theost.walletok.data.models.TransactionCategoryType
import com.theost.walletok.delegates.TypeItem

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
        val typeItems = mutableListOf<TypeItem>()
        typeItems.addAll(_allData.value!!)
        val isSelected = !typeItems[position].isSelected
        typeItems.forEach { it.isSelected = false }
        typeItems[position].isSelected = isSelected
        _allData.value = typeItems
    }

}