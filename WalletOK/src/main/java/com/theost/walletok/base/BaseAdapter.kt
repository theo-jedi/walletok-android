package com.theost.walletok.base

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class BaseAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data = mutableListOf<Any>()
    private val delegates = mutableListOf<AdapterDelegate>()

    fun addDelegate(delegate: AdapterDelegate) {
        delegates.add(delegate)
    }

    fun getDelegateClassByPos(position: Int) = delegates[getItemViewType(position)]::class

    @SuppressLint("NotifyDataSetChanged")
    fun setData(new: List<Any>) {
        data.clear()
        data.addAll(new)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegates[viewType].onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[getItemViewType(position)].onBindViewHolder(holder, data[position], position)
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        return delegates.indexOfFirst { it.isOfViewType(data[position]) }
    }
}