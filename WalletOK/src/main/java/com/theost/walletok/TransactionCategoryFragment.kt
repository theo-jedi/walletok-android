package com.theost.walletok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.databinding.FragmentTransactionCategoryBinding
import com.theost.walletok.delegates.CategoryAdapterDelegate
import com.theost.walletok.delegates.CategoryItem
import com.theost.walletok.widgets.TransactionCategoryListener
import io.reactivex.android.schedulers.AndroidSchedulers

class TransactionCategoryFragment : Fragment() {

    companion object {
        private const val TRANSACTION_CATEGORY_KEY = "transaction_category"
        private const val TRANSACTION_TYPE_KEY = "transaction_type"
        private const val TRANSACTION_CATEGORY_UNSET = -1

        fun newFragment(savedCategory: Int?, savedType: String?): Fragment {
            val fragment = TransactionCategoryFragment()
            val bundle = Bundle()
            bundle.putInt(TRANSACTION_CATEGORY_KEY, savedCategory ?: TRANSACTION_CATEGORY_UNSET)
            bundle.putString(TRANSACTION_TYPE_KEY, savedType ?: "")
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentTransactionCategoryBinding
    private lateinit var categoryItems: List<CategoryItem>

    private var savedCategory: Int = TRANSACTION_CATEGORY_UNSET
    private var savedType: String = ""

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

        savedType = arguments?.getString(TRANSACTION_TYPE_KEY) ?: ""
        savedCategory = arguments?.getInt(TRANSACTION_CATEGORY_KEY) ?: TRANSACTION_CATEGORY_UNSET
        if (savedCategory != TRANSACTION_CATEGORY_UNSET) binding.submitButton.isEnabled = true

        binding.listCategory.setHasFixedSize(true)
        CategoriesRepository.getCategories().subscribeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { list ->
                categoryItems =
                    list.filter { category -> category.type.uiName == savedType }.map { category ->
                        CategoryItem(
                            id = category.id,
                            name = category.name,
                            icon = category.image as Int,
                            isSelected = savedCategory == category.id
                        )
                    }
                val adapter = BaseAdapter()
                adapter.addDelegate(CategoryAdapterDelegate { onItemClicked(it) })
                binding.listCategory.adapter = adapter
                adapter.setData(categoryItems)
            }.subscribe()

        binding.submitButton.setOnClickListener {
            setCurrentCategory()
        }

        return binding.root
    }

    private fun onItemClicked(position: Int) {
        val category = categoryItems[position]
        savedCategory = category.id
        if (savedCategory != TRANSACTION_CATEGORY_UNSET) {
            binding.submitButton.isEnabled = true
        }
    }

    private fun setCurrentCategory() {
        (activity as TransactionCategoryListener).onCategorySubmitted(savedCategory)
    }

}