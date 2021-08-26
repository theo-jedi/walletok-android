package com.theost.walletok.utils

import com.theost.walletok.delegates.CategoryItem
import com.theost.walletok.delegates.TypeItem

object ModelUtils {

    fun selectTypeData(list: List<TypeItem>, position: Int) : List<TypeItem> {
        val selectedName = list[position].name
        val typeItems = list.map {
            TypeItem(
                name = it.name,
                isSelected = if (it.name == selectedName) !it.isSelected else false
            )
        }
        return typeItems
    }

    fun selectCategoryData(list: List<CategoryItem>, position: Int, isSingle: Boolean) : List<CategoryItem> {
        val selectedId = list[position].id
        val categoryItems = list.map {
            CategoryItem(
                id = it.id,
                name = it.name,
                iconUrl = it.iconUrl,
                iconColor = it.iconColor,
                isSelected = if (it.id == selectedId) !it.isSelected else if (!isSingle) it.isSelected else false
            )
        }
        return categoryItems
    }

}