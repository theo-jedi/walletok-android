package com.theost.walletok

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class WalletDetailsAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val data = mutableListOf<Any>()
    private val _delegates = mutableListOf<AdapterDelegate>()
    val delegates: List<AdapterDelegate> = _delegates

    fun addDelegate(delegate: AdapterDelegate) {
        _delegates.add(delegate)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun setData(new: List<Any>) {
        data.clear()
        data.addAll(new)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return _delegates[viewType].onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        _delegates[getItemViewType(position)].onBindViewHolder(holder, data[position], position)
    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        return _delegates.indexOfFirst { it.isOfViewType(data[position]) }
    }
}