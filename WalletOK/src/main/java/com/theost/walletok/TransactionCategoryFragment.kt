package com.theost.walletok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.theost.walletok.databinding.FragmentTransactionCategoryBinding
import com.theost.walletok.utils.ViewUtils
import com.theost.walletok.widgets.TransactionCategoryAdapter
import com.theost.walletok.widgets.TransactionCategoryListener

class TransactionCategoryFragment : Fragment() {

    companion object {
        private const val TRANSACTION_CATEGORY_KEY = "transaction_category"

        fun newFragment(savedCategory: String): Fragment {
            val fragment = TransactionCategoryFragment()
            val bundle = Bundle()
            bundle.putString(TRANSACTION_CATEGORY_KEY, savedCategory)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentTransactionCategoryBinding

    private val savedCategory: String?
        get() = arguments?.getString(TRANSACTION_CATEGORY_KEY)

    private var lastSelected = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionCategoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            setCurrentCategory()
        }

        val categories = listOf("Зарплата", "Подработка", "Капитализация")
        binding.listCategory.adapter = TransactionCategoryAdapter(categories, savedCategory.orEmpty()) {
            onItemClicked(it)
        }

        return binding.root
    }

    private fun onItemClicked(position: Int) {
        if (lastSelected != position) {
            ViewUtils.enableSubmitButton(requireContext(), binding.submitButton)
            if (lastSelected != -1) {
                binding.listCategory.layoutManager?.findViewByPosition(lastSelected)
                    ?.findViewById<ImageView>(R.id.category_check)?.visibility = View.INVISIBLE
            }
            lastSelected = position
        }
    }

    private fun setCurrentCategory() {
        val category = (binding.listCategory.adapter as TransactionCategoryAdapter).getItem(lastSelected)
        (activity as TransactionCategoryListener).onCategorySubmitted(category)
    }

}