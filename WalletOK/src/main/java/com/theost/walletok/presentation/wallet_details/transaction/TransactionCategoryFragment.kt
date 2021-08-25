package com.theost.walletok.presentation.wallet_details.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.theost.walletok.R
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.databinding.FragmentTransactionCategoryBinding
import com.theost.walletok.delegates.*
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
    private lateinit var categoryItems: MutableList<Any>

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

        adapter.apply {
            addDelegate(CategoryAdapterDelegate {
                    position, categoryId -> onItemClicked(position, categoryId)
            })
            addDelegate(ButtonAdapterDelegate { type ->
                when (type) {
                    ListButtonType.CREATION -> (activity as TransactionCategoryListener).onCreateCategoryClicked()
                    ListButtonType.DELETION -> (activity as TransactionCategoryListener).onDeleteCategoryClicked()
                }
            })
        }

        binding.listCategory.adapter = adapter
        binding.listCategory.setHasFixedSize(true)

        loadCategories()

        binding.submitButton.setOnClickListener {
            (activity as TransactionCategoryListener).onCategorySubmitted(savedCategory)
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

    private fun onItemClicked(position: Int, categoryId: Int) {
        if (categoryItems[position] is CategoryItem && (lastSelected == TRANSACTION_CATEGORY_UNSET || categoryItems[lastSelected] is CategoryItem)) {
            if (lastSelected != TRANSACTION_CATEGORY_UNSET) (categoryItems[lastSelected] as CategoryItem).isSelected =
                false
            (categoryItems[position] as CategoryItem).isSelected = true
            adapter.setData(categoryItems)

            savedCategory = categoryId
            lastSelected = position

            if (savedCategory != TRANSACTION_CATEGORY_UNSET) {
                binding.submitButton.isEnabled = true
            }
        }
    }

    private fun loadCategories() {
        CategoriesRepository.getCategories().subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                categoryItems = list
                    .filter { category -> category.type.uiName == savedType }
                    .map { category ->
                        CategoryItem(
                            id = category.id,
                            name = category.name,
                            icon = category.image as Int,
                            isSelected = savedCategory == category.id
                        )
                    }.toMutableList()
                categoryItems.addAll(
                    listOf(
                        ListButton(
                            text = getString(R.string.delete_category),
                            ListButtonType.DELETION,
                            isVisible = true,
                            isEnabled = true
                        ),
                        ListButton(
                            text = getString(R.string.create_category),
                            ListButtonType.CREATION,
                            isVisible = true,
                            isEnabled = true
                        )
                    )
                )
                lastSelected = categoryItems.indexOfFirst { it is CategoryItem && it.id == savedCategory }
                if (lastSelected == TRANSACTION_CATEGORY_UNSET) savedCategory = TRANSACTION_CATEGORY_UNSET
                adapter.setData(categoryItems)
            }, {
                ErrorMessageHelper.setUpErrorMessage(binding.errorWidget) {
                    loadCategories()
                }
            }).addTo(compositeDisposable)
    }

    private fun showErrorToast() {
        Toast.makeText(requireContext(), getString(R.string.not_available), Toast.LENGTH_SHORT)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}