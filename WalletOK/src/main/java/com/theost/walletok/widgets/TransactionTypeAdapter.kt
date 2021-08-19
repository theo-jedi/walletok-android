package com.theost.walletok.widgets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.R

class TransactionTypeAdapter(private val list: List<String>, private val selectedType: String, private val clickListener: (position: Int) -> Unit) :
    RecyclerView.Adapter<TransactionTypeAdapter.TransactionTypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionTypeViewHolder {
        return TransactionTypeViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun onBindViewHolder(
        holder: TransactionTypeViewHolder,
        position: Int
    ) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun getItem(position: Int) : String = list[position]

    inner class TransactionTypeViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_list_type, parent, false)),
        View.OnClickListener {

        private var mTypeView: TextView? = null

        init {
            mTypeView = itemView.findViewById(R.id.type_title)
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            itemView.findViewById<ImageView>(R.id.type_check).visibility = View.VISIBLE
            clickListener.invoke(adapterPosition)
        }

        fun bind(transaction: String) {
            itemView.findViewById<ImageView>(R.id.type_check).visibility = View.INVISIBLE
            mTypeView?.text = transaction
            if (mTypeView?.text == selectedType) {
                itemView.callOnClick()
            }
        }

    }

}