package com.theost.walletok

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.theost.walletok.databinding.FragmentTransactionValueBinding
import com.theost.walletok.utils.ViewUtils

class TransactionValueFragment : Fragment() {

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
            nextFragment()
        }

        binding.inputValue.addTextChangedListener {
            onTextChanged(it.toString())
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isValueValid(input: String) : Boolean = input.trim() != ""

    private fun onTextChanged(input: String) {
        if (isValueValid(input)) {
            ViewUtils.enableSubmitButton(requireContext(), binding.submitButton)
        } else {
            ViewUtils.disableSubmitButton(requireContext(), binding.submitButton)
        }
    }

    private fun nextFragment() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.creation_fragment_container, TransactionTypeFragment())
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }

}