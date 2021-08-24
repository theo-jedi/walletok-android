package com.theost.walletok.delegates

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.presentation.base.AdapterDelegate
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

    class ViewHolder(
        private val binding: ItemListPreferenceBinding,
        private val clickListener: (preferenceName: String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(preference: TransactionPreference) {
            binding.root.setOnClickListener { clickListener(preference.type.uiName) }
            binding.preferenceName.text = preference.type.uiName
            binding.preferenceValue.text = preference.value
            binding.root.isEnabled = preference.isEnabled
            binding.preferenceArrow.visibility = if (preference.isEnabled) View.VISIBLE else View.GONE
        }

    }

}

data class TransactionPreference(
    val type: PreferenceType,
    val value: String,
    var isEnabled: Boolean
)

enum class PreferenceType(val uiName: String) {
    VALUE("Сумма"), TYPE("Тип"), CATEGORY("Категория"),
    DATE("Дата операции"), NAME("Название"), ICON("Иконка")
}