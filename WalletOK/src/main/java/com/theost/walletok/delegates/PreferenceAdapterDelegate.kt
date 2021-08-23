package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.AdapterDelegate
import com.theost.walletok.databinding.ItemListPreferenceBinding

class PreferenceAdapterDelegate(
    private val clickListener: (preferenceName: String) -> Unit
) : AdapterDelegate {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = ItemListPreferenceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, clickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: Any, position: Int) {
        (holder as ViewHolder).bind(item as TransactionPreference)
    }

    override fun isOfViewType(item: Any): Boolean = item is TransactionPreference

    class ViewHolder(private val binding: ItemListPreferenceBinding, private val clickListener: (preferenceName: String) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(preference: TransactionPreference) {
            binding.root.setOnClickListener { clickListener(preference.type.uiName) }
            binding.preferenceName.text = preference.type.uiName
            binding.preferenceValue.text = preference.value
            binding.root.isClickable = preference.isClickable
            binding.preferenceArrow.visibility =
                if (preference.isClickable) View.VISIBLE else View.GONE
        }

    }

}

data class TransactionPreference(
    val type: TransactionPreferenceType,
    val value: String,
    val isClickable: Boolean
)

enum class TransactionPreferenceType(val uiName: String) {
    VALUE("Сумма"), TYPE("Тип"), CATEGORY("Категория"), DATE("Дата операции")
}