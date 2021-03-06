package com.theost.walletok.presentation.wallet_details.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theost.walletok.R
import com.theost.walletok.databinding.FragmentTransactionCategoryBinding
import com.theost.walletok.delegates.CategoryAdapterDelegate
import com.theost.walletok.presentation.base.DiffAdapter
import com.theost.walletok.presentation.wallet_details.transaction.widgets.CategoryListener
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.ViewUtils
import io.reactivex.disposables.CompositeDisposable

class CategoryDeleteFragment : Fragment() {

    companion object {

        fun newFragment(): Fragment {
            return CategoryDeleteFragment()
        }
    }

    private var _binding: FragmentTransactionCategoryBinding? = null
    private val binding get() = _binding!!

    private val compositeDisposable = CompositeDisposable()
    private val adapter = DiffAdapter()

    private val viewModel: UserCategoriesViewModel by viewModels()

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

        adapter.apply {
            addDelegate(CategoryAdapterDelegate { position ->
                onItemClicked(position)
            })
        }

        binding.listCategory.adapter = adapter
        binding.listCategory.setHasFixedSize(true)

        binding.submitButton.text = getString(R.string.delete)
        binding.submitButton.setOnClickListener {
            viewModel.deleteSelectedData()
        }

        binding.errorWidget.closeButton.setOnClickListener {
            ViewUtils.hideErrorMessage(binding.errorWidget.errorLayout)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            if (it is Resource.Error) {
                ViewUtils.showErrorMessage(binding.errorWidget.errorLayout)
            } else if (it is Resource.Success) {
                ViewUtils.hideErrorMessage(binding.errorWidget.errorLayout)
                if (binding.submitButton.isEnabled) (activity as CategoryListener).onCategoryDeleted()
            }
        }

        viewModel.allData.observe(viewLifecycleOwner) { list ->
            binding.submitButton.isEnabled = list.find { it.isSelected } != null
            binding.emptyList.emptyLayout.visibility = if (list.isNotEmpty()) View.GONE else View.VISIBLE

            adapter.submitList(list)
        }

        viewModel.loadData()

        return binding.root
    }

    private fun onItemClicked(position: Int) {
        viewModel.selectData(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        compositeDisposable.dispose()
    }
}