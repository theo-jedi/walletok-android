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

        val bundle = this.arguments
        if (bundle != null) {
            val title = bundle.getString(TransactionActivity.TRANSACTION_DATA_KEY, "")
            if (title != "") binding.toolbar.title = title
        }

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            (activity as TransactionListener).onCreateTransaction()
        }

        val value = (activity as TransactionActivity).transaction.value + " " + getString(R.string.wallet_rub)
        binding.transactionValue.text = value

        val type = (activity as TransactionActivity).transaction.type
        binding.transactionType.text = type

        val text = (activity as TransactionActivity).transaction.category
        binding.transactionCategory.text = text

        binding.layoutValue.setOnClickListener { updateCurrentValue() }
        binding.layoutType.setOnClickListener { updateCurrentType() }
        binding.layoutCategory.setOnClickListener { updateCurrentCategory() }

        if ((activity as TransactionActivity).transaction.date == "") {
            val currentDate = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date())
            binding.transactionDate.text = currentDate
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateCurrentValue() {
        (activity as TransactionListener).onEditValue()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    private fun updateCurrentType() {
        (activity as TransactionListener).onEditType()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    private fun updateCurrentCategory() {
        (activity as TransactionListener).onEditCategory()
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

}