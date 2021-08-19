package com.theost.walletok.delegates

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.AdapterDelegate
import com.theost.walletok.databinding.ItemEmptyListBinding

class EmptyListAdapterDelegate : AdapterDelegate {
    private lateinit var parent: ViewGroup
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        this.parent = parent
        val binding = ItemEmptyListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        val params = binding.root.layoutParams
        val value = TypedValue()
        parent.context.theme.resolveAttribute(android.R.attr.actionBarSize, value, true)
        val actionBarSize =
            TypedValue.complexToDimensionPixelSize(value.data, parent.resources.displayMetrics)
        params.height = parent.height - parent[parent.childCount - 1].bottom - actionBarSize
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        // Nothing to do
    }

    override fun isOfViewType(item: Any) = item is EmptyListContent

    class ViewHolder(binding: ItemEmptyListBinding) :
        RecyclerView.ViewHolder(binding.root)
}

object EmptyListContent