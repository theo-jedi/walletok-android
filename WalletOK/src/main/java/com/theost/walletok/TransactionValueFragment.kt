package com.theost.walletok

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.walletok.databinding.FragmentTransactionValueBinding
import com.theost.walletok.utils.StringUtils
import com.theost.walletok.widgets.TransactionValueListener


class TransactionValueFragment : Fragment() {

    companion object {
        private const val TRANSACTION_VALUE_KEY = "transaction_value"

        fun newFragment(savedValue: Int?): Fragment {
            val fragment = TransactionValueFragment()
            val bundle = Bundle()
            bundle.putInt(TRANSACTION_VALUE_KEY, savedValue ?: 0)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentTransactionValueBinding

    private val savedValue: Int?
        get() = arguments?.getInt(TRANSACTION_VALUE_KEY)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionValueBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.submitButton.setOnClickListener { setCurrentValue() }

        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.inputValue.removeTextChangedListener(this)
                onTextChanged(s.toString())
                binding.inputValue.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.inputValue.addTextChangedListener(textWatcher)

        binding.inputValue.setText(
            if (savedValue != null && savedValue != 0) StringUtils.convertMoneyForDisplay(
                savedValue!!
            ) else ""
        )

        return binding.root
    }

    private fun onTextChanged(input: String) {
        val value = StringUtils.formatMoney(input)

        binding.inputValue.setText(value)
        binding.inputValue.setSelection(binding.inputValue.length())

        updateSubmitButton(input)
    }

    private fun updateSubmitButton(input: String) {
        binding.submitButton.isEnabled = StringUtils.isMoneyValueValid(input)
    }

    private fun setCurrentValue() {
        val value = StringUtils.convertMoneyForStorage(binding.inputValue.text.toString())
        (activity as TransactionValueListener).onValueSubmitted(value)
    }

}