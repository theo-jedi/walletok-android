package com.theost.walletok.presentation.base

import androidx.recyclerview.widget.RecyclerView

class PaginationAdapter(private val paginationAdapterHelper: PaginationAdapterHelper) :
    BaseAdapter() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        paginationAdapterHelper.onBind(position, itemCount)
    }
}