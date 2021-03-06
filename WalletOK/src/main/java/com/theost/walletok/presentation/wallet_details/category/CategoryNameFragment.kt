package com.theost.walletok.presentation.wallet_details.category

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import com.theost.walletok.R
import com.theost.walletok.databinding.FragmentCategoryNameBinding
import com.theost.walletok.presentation.wallet_details.transaction.widgets.CategoryNameListener


class CategoryNameFragment : Fragment() {

    companion object {
        private const val CATEGORY_NAME_KEY = "category_value"

        fun newFragment(savedName: String?): Fragment {
            val fragment = CategoryNameFragment()
            val bundle = Bundle()
            bundle.putString(CATEGORY_NAME_KEY, savedName)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var _binding: FragmentCategoryNameBinding? = null
    private val binding get() = _binding!!

    private val savedName: String
        get() = arguments?.getString(CATEGORY_NAME_KEY) ?: ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryNameBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            binding.inputName.onEditorAction(EditorInfo.IME_ACTION_DONE)
            setCurrentName()
        }

        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateSubmitButton(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.inputName.addTextChangedListener(textWatcher)
        binding.inputName.setText(savedName)

        return binding.root
    }

    private fun updateSubmitButton(input: String) {
        binding.submitButton.isEnabled = input.trim() != ""
    }

    private fun setCurrentName() {
        val name = binding.inputName.text.toString().trim()
        (activity as CategoryNameListener).onCategoryNameSubmitted(name)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}