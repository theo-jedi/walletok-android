package com.theost.walletok.presentation.base

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class DelegateAdapterItemCallback : DiffUtil.ItemCallback<DelegateItem>() {
    override fun areItemsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem::class == newItem::class && oldItem.id() == newItem.id()

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean =
        oldItem.content() == newItem.content()
}