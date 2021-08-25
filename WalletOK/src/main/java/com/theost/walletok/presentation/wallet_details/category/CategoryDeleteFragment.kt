package com.theost.walletok.presentation.wallet_details.category

import android.annotation.SuppressLint
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
import com.theost.walletok.presentation.wallet_details.transaction.TransactionCategoryFragment
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionCategoryListener
import com.theost.walletok.utils.addTo
import com.theost.walletok.widgets.CategoryListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class CategoryDeleteFragment : Fragment() {

    companion object {

        fun newFragment(): Fragment {
            return CategoryDeleteFragment()
        }
    }

    private lateinit var binding: FragmentTransactionCategoryBinding
    private lateinit var categoryItems: List<CategoryItem>

    private val compositeDisposable = CompositeDisposable()
    private val adapter = BaseAdapter()
    private val selectedCategories: MutableList<Int> = mutableListOf()

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

        adapter.apply {
            addDelegate(CategoryAdapterDelegate {
                position, categoryId -> onItemClicked(position, categoryId)
            })
        }

        binding.listCategory.adapter = adapter
        binding.listCategory.setHasFixedSize(true)

        loadCategories()

        binding.submitButton.text = getString(R.string.delete)
        binding.submitButton.setOnClickListener {
            deleteSelectedCategories()
        }

        return binding.root
    }

    private fun onItemClicked(position: Int, categoryId: Int) {
        if (categoryId in selectedCategories) {
            categoryItems[position].isSelected = false
            selectedCategories.remove(categoryId)
        } else {
            categoryItems[position].isSelected = true
            selectedCategories.add(categoryId)
        }
        adapter.setData(categoryItems)

        binding.submitButton.isEnabled = selectedCategories.size > 0
    }

    private fun deleteSelectedCategories() {
        CategoriesRepository.removeCategories(selectedCategories).subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                (activity as CategoryListener).onCategoryDeleted()
            }, {
                ErrorMessageHelper.setUpErrorMessage(binding.errorWidget) {
                    loadCategories()
                }
            }).addTo(compositeDisposable)
    }

    private fun loadCategories() {
        CategoriesRepository.getCategories().subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                categoryItems = list
                    .map { category ->
                        CategoryItem(
                            id = category.id,
                            name = category.name,
                            icon = category.image as Int,
                            isSelected = false
                        )
                    }
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