package com.theost.walletok.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.R

class TransactionCategoryAdapter(private val list: List<String>, private val clickListener: (position: Int) -> Unit) :
    RecyclerView.Adapter<TransactionCategoryAdapter.TransactionCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionCategoryViewHolder {
        return TransactionCategoryViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(
        holder: TransactionCategoryViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class TransactionCategoryViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_list_category, parent, false)),
        View.OnClickListener {

        private var mTypeView: TextView? = null

        init {
            mTypeView = itemView.findViewById(R.id.category_title)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemView.findViewById<ImageView>(R.id.category_check).visibility = View.VISIBLE
            clickListener.invoke(adapterPosition)
        }

        fun bind(transaction: String) {
            itemView.findViewById<ImageView>(R.id.category_check).visibility = View.INVISIBLE
            mTypeView?.text = transaction
        }

    }

}