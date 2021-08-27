package com.theost.walletok.presentation.wallet_details.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theost.walletok.R
import com.theost.walletok.databinding.FragmentTransactionCategoryBinding
import com.theost.walletok.delegates.ButtonAdapterDelegate
import com.theost.walletok.delegates.CategoryAdapterDelegate
import com.theost.walletok.delegates.ListButton
import com.theost.walletok.delegates.ListButtonType
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.presentation.base.DelegateItem
import com.theost.walletok.presentation.base.DiffAdapter
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionCategoryListener
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.ViewUtils
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

    private var _binding: FragmentTransactionCategoryBinding? = null
    private val binding get() = _binding!!

    private val compositeDisposable = CompositeDisposable()
    private var savedCategory: Int = TRANSACTION_CATEGORY_UNSET
    private var savedType: String = ""

    private val viewModel: TransactionCategoryViewModel by viewModels()
    private val adapter = DiffAdapter()

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

        viewModel.allData.observe(viewLifecycleOwner) { list ->
            val listItems = mutableListOf<DelegateItem>()
            listItems.addAll(list)
            listItems.addAll(listOf(
                ListButton(
                    text = getString(R.string.delete_category),
                    type = ListButtonType.DELETION,
                    isVisible = true,
                    isEnabled = true
                ),
                ListButton(
                    text = getString(R.string.create_category),
                    type = ListButtonType.CREATION,
                    isVisible = true,
                    isEnabled = true
                ))
            )

            val item = list.find { it.isSelected }
            if (item != null) {
                savedCategory = item.id
                binding.submitButton.isEnabled = true
            } else {
                binding.submitButton.isEnabled = false
            }

            adapter.submitList(listItems)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            if (it is Resource.Error) {
                ViewUtils.showErrorMessage(binding.errorWidget.errorLayout)
            } else {
                ViewUtils.hideErrorMessage(binding.errorWidget.errorLayout)
            }
            binding.transactionProgress.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
        }

        binding.errorWidget.closeButton.setOnClickListener {
            ViewUtils.hideErrorMessage(binding.errorWidget.errorLayout)
        }

        adapter.apply {
            addDelegate(CategoryAdapterDelegate { position ->
                onItemClicked(position)
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

        binding.submitButton.setOnClickListener {
            (activity as TransactionCategoryListener).onCategorySubmitted(savedCategory)
        }

        viewModel.loadData(savedCategory, savedType)

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

    private fun onItemClicked(position: Int) {
        viewModel.selectData(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        compositeDisposable.dispose()
    }
}