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
import com.theost.walletok.widgets.TransactionListener

class TransactionCategoryFragment : Fragment() {

    companion object {
        fun newFragment(): Fragment {
            return TransactionCategoryFragment()
        }
    }

    private var _binding: FragmentTransactionCategoryBinding? = null
    private val binding get() = _binding!!
    private var lastSelected = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionCategoryBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            setCurrentCategory()
        }

        val bundle = arguments
        val selectedCategory = if (bundle != null)
            bundle.getString(TransactionActivity.TRANSACTION_CATEGORY_KEY, "") else ""

        val categories = listOf("Зарплата", "Подработка", "Капитализация")
        binding.listCategory.adapter = TransactionCategoryAdapter(categories, selectedCategory) {
            onItemClicked(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        (activity as TransactionListener).onSetTransactionData(category, TransactionActivity.TRANSACTION_CATEGORY_KEY)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

}