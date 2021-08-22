package com.theost.walletok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.databinding.FragmentTransactionEditBinding
import com.theost.walletok.utils.DateTimeUtils
import com.theost.walletok.utils.StringUtils
import com.theost.walletok.widgets.TransactionListener
import io.reactivex.android.schedulers.AndroidSchedulers

class TransactionEditFragment : Fragment() {

    companion object {
        private const val TRANSACTION_MODEL_KEY = "transaction_model"

        fun newFragment(transaction: TransactionCreationModel): Fragment {
            val fragment = TransactionEditFragment()
            val bundle = Bundle()
            bundle.putParcelable(TRANSACTION_MODEL_KEY, transaction)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentTransactionEditBinding
    private var categoryName: String? = null

    private val transaction: TransactionCreationModel?
        get() = arguments?.getParcelable(TRANSACTION_MODEL_KEY)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionEditBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        val transactionListener = activity as TransactionListener
        binding.layoutValue.setOnClickListener { transactionListener.onValueEdit() }
        binding.layoutType.setOnClickListener { transactionListener.onTypeEdit() }
        binding.layoutCategory.setOnClickListener { transactionListener.onCategoryEdit() }

        binding.submitButton.setOnClickListener {
            transactionListener.onTransactionSubmitted()
        }

        CategoriesRepository.getCategories().subscribeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { list ->
                categoryName = list.find { item -> item.id == transaction?.category }?.name
                loadTransactionData()
            }.subscribe()

        return binding.root
    }

    private fun loadTransactionData() {
        if (transaction != null) {
            val value = StringUtils.formatMoney(
                StringUtils.convertMoneyForDisplay(
                    transaction?.value ?: 0
                )
            ) + " " + (transaction?.currency ?: getString(R.string.wallet_rub))
            binding.transactionValue.text = value
            binding.transactionType.text = transaction?.type ?: ""
            binding.transactionCategory.text = categoryName ?: ""
            binding.transactionDate.text = transaction?.dateTime ?: DateTimeUtils.getCurrentDate()
        }
    }

}