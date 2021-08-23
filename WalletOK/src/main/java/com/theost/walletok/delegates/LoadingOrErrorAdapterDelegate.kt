package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.presentation.base.AdapterDelegate
import com.theost.walletok.presentation.base.PaginationStatus
import com.theost.walletok.databinding.ItemLoadingOrErrorBinding

class LoadingOrErrorAdapterDelegate(private val listener: View.OnClickListener) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemLoadingOrErrorBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as PaginationStatus)
    }

    class ViewHolder(
        val binding: ItemLoadingOrErrorBinding,
        private val listener: View.OnClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PaginationStatus) {
            when (item) {
                PaginationStatus.Loading -> {
                    binding.itemErrorLayout.visibility = View.GONE
                    binding.progressCircular.visibility = View.VISIBLE
                }
                PaginationStatus.Error -> {
                    binding.itemErrorLayout.visibility = View.VISIBLE
                    binding.progressCircular.visibility = View.GONE
                    binding.itemRetryButton.setOnClickListener(listener)
                }
                else -> {
                }
            }
        }
    }

    override fun isOfViewType(item: Any) = item is PaginationStatus
}