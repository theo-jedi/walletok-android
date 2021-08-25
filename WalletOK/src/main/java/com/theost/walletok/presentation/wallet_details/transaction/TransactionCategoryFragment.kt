package com.theost.walletok.presentation.wallet_details.transaction

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.walletok.R
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.databinding.FragmentTransactionCategoryBinding
import com.theost.walletok.delegates.CategoryAdapterDelegate
import com.theost.walletok.delegates.CategoryItem
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.presentation.base.ErrorMessageHelper
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionCategoryListener
import com.theost.walletok.utils.addTo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class TransactionCategoryFragment : Fragment() {

    companion object {
        private const val TRANSACTION_CATEGORY_KEY = "transaction_category"
        private const val TRANSACTION_TYPE_KEY = "transaction_type"
        private const val TRANSACTION_CATEGORY_UNSET = -1

        fun newFragment(
            savedCategory: Int? = TRANSACTION_CATEGORY_UNSET,
            savedType: String? = ""
        ): Fragment {
            val fragment = TransactionCategoryFragment()
            val bundle = Bundle()
            bundle.putInt(TRANSACTION_CATEGORY_KEY, savedCategory ?: TRANSACTION_CATEGORY_UNSET)
            bundle.putString(TRANSACTION_TYPE_KEY, savedType)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentTransactionCategoryBinding
    private lateinit var categoryItems: List<CategoryItem>

    private val compositeDisposable = CompositeDisposable()
    private var savedCategory: Int = TRANSACTION_CATEGORY_UNSET
    private var lastSelected: Int = TRANSACTION_CATEGORY_UNSET
    private var savedType: String = ""
    private val adapter = BaseAdapter()

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

        if (savedCategory != TRANSACTION_CATEGORY_UNSET) {
            binding.submitButton.isEnabled = true
        }

        adapter.addDelegate(CategoryAdapterDelegate { onItemClicked(it) })

        binding.listCategory.adapter = adapter
        binding.listCategory.setHasFixedSize(true)

        loadCategories()

        binding.submitButton.setOnClickListener {
            setCurrentCategory()
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            savedCategory =
                arguments?.getInt(TRANSACTION_CATEGORY_KEY) ?: TRANSACTION_CATEGORY_UNSET
            savedType = arguments?.getString(TRANSACTION_TYPE_KEY) ?: ""
        } else {
            savedCategory =
                savedInstanceState.getInt(TRANSACTION_CATEGORY_KEY, TRANSACTION_CATEGORY_UNSET)
            savedType = savedInstanceState.getString(TRANSACTION_TYPE_KEY) ?: ""
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(TRANSACTION_CATEGORY_KEY, savedCategory)
        outState.putString(TRANSACTION_TYPE_KEY, savedType)
        super.onSaveInstanceState(outState)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onItemClicked(position: Int) {
        if (lastSelected != TRANSACTION_CATEGORY_UNSET) categoryItems[lastSelected].isSelected =
            false
        categoryItems[position].isSelected = true
        adapter.setData(categoryItems)

        savedCategory = categoryItems[position].id
        lastSelected = position

        if (savedCategory != TRANSACTION_CATEGORY_UNSET) {
            binding.submitButton.isEnabled = true
        }
    }

    private fun setCurrentCategory() {
        (activity as TransactionCategoryListener).onCategorySubmitted(savedCategory)
    }

    private fun loadCategories() {
        CategoriesRepository.getCategories().observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                categoryItems =
                    list.filter { category -> category.type.uiName == savedType }.map { category ->
                        CategoryItem(
                            id = category.id,
                            name = category.name,
                            iconUrl = category.iconLink,
                            iconColor = category.iconColor,
                            isSelected = savedCategory == category.id
                        )
                    }
                lastSelected = categoryItems.indexOfFirst { it.id == savedCategory }
                adapter.setData(categoryItems)
            }, {
                ErrorMessageHelper.setUpErrorMessage(binding.errorWidget) {
                    loadCategories()
                }
            }).addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}