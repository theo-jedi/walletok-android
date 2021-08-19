package com.theost.walletok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.walletok.databinding.FragmentTransactionEditBinding
import com.theost.walletok.widgets.TransactionListener
import java.text.SimpleDateFormat
import java.util.*

class TransactionEditFragment : Fragment() {

    companion object {
        fun newFragment(): Fragment {
            return TransactionEditFragment()
        }
    }

    private var _binding: FragmentTransactionEditBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionEditBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            (activity as TransactionListener).onCreateTransaction()
        }

        binding.layoutValue.setOnClickListener { updateCurrentData(TransactionActivity.TRANSACTION_VALUE_KEY) }
        binding.layoutType.setOnClickListener { updateCurrentData(TransactionActivity.TRANSACTION_TYPE_KEY) }
        binding.layoutCategory.setOnClickListener { updateCurrentData(TransactionActivity.TRANSACTION_CATEGORY_KEY) }

        loadTransactionData()

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadTransactionData() {
        val value = (activity as TransactionActivity).transaction.value + " " + getString(R.string.wallet_rub)
        binding.transactionValue.text = value

        val type = (activity as TransactionActivity).transaction.type
        binding.transactionType.text = type

        val text = (activity as TransactionActivity).transaction.category
        binding.transactionCategory.text = text

        if ((activity as TransactionActivity).transaction.date == "") {
            val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            binding.transactionDate.text = currentDate
        }
    }

    private fun updateCurrentData(key: String) {
        (activity as TransactionListener).onEditTransactionData(key)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

}