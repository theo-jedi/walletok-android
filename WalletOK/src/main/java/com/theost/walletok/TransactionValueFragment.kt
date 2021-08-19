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
import com.theost.walletok.utils.ViewUtils
import com.theost.walletok.widgets.TransactionListener


class TransactionValueFragment : Fragment() {

    companion object {
        fun newFragment(): Fragment {
            return TransactionValueFragment()
        }
    }

    private var _binding: FragmentTransactionValueBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionValueBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            setCurrentValue()
        }

        var isChanging = false
        binding.inputValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isChanging) {
                    isChanging = true
                    onTextChanged(s.toString())
                    isChanging = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        val bundle = this.arguments
        if (bundle != null) binding.inputValue.setText(bundle.getString(TransactionActivity.TRANSACTION_DATA_KEY, ""))

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onTextChanged(input: String) {
        val value = StringUtils.formatCurrency(input)
        binding.inputValue.setText(value)
        binding.inputValue.setSelection(binding.inputValue.length())
        if (StringUtils.isCurrencyValueValid(input)) {
            ViewUtils.enableSubmitButton(requireContext(), binding.submitButton)
        } else {
            ViewUtils.disableSubmitButton(requireContext(), binding.submitButton)
        }
    }

    private fun setCurrentValue() {
        val value = binding.inputValue.text.toString().trim()
        (activity as TransactionListener).onSetValue(value)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

}