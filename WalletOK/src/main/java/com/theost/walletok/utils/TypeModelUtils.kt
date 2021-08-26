package com.theost.walletok.utils

import com.theost.walletok.delegates.TypeItem

object TypeModelUtils {

    fun selectData(list: List<TypeItem>, position: Int) : MutableList<TypeItem> {
        val typeItems = mutableListOf<TypeItem>()
        typeItems.addAll(list)
        val isSelected = !typeItems[position].isSelected
        typeItems.forEach { it.isSelected = false }
        typeItems[position].isSelected = isSelected
        return typeItems
    }

}