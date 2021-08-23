package com.theost.walletok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.databinding.FragmentTransactionEditBinding
import com.theost.walletok.delegates.*
import com.theost.walletok.utils.DateTimeUtils
import com.theost.walletok.utils.StringUtils
import com.theost.walletok.widgets.TransactionListener
import io.reactivex.android.schedulers.AndroidSchedulers

class TransactionEditFragment : Fragment() {

    companion object {
        private const val TRANSACTION_MODEL_KEY = "transaction_model"
        private const val TRANSACTION_TITLE_KEY = "transaction_title"

        fun newFragment(transaction: TransactionCreationModel, title: Int): Fragment {
            val fragment = TransactionEditFragment()
            val bundle = Bundle()
            bundle.putParcelable(TRANSACTION_MODEL_KEY, transaction)
            bundle.putInt(TRANSACTION_TITLE_KEY, title)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var transactionListener: TransactionListener
    private lateinit var binding: FragmentTransactionEditBinding
    private var categoryName: String? = null

    private val transaction: TransactionCreationModel?
        get() = arguments?.getParcelable(TRANSACTION_MODEL_KEY)
    private val title: Int
        get() = arguments?.getInt(TRANSACTION_TITLE_KEY) ?: R.string.new_transaction

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionEditBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.title = getString(title)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        transactionListener = activity as TransactionListener

        binding.listPreferences.setHasFixedSize(true)

        CategoriesRepository.getCategories().subscribeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { list ->
                categoryName = list.find { item -> item.id == transaction?.category }?.name
                val adapter = BaseAdapter()
                adapter.apply {
                    addDelegate(PreferenceAdapterDelegate { onPreferenceClicked(it) })
                    addDelegate(TitleAdapterDelegate())
                }
                binding.listPreferences.adapter = adapter
                adapter.setData(getPreferencesList())
            }.subscribe()

        binding.submitButton.setOnClickListener {
            transactionListener.onTransactionSubmitted()
        }

        return binding.root
    }

    private fun onPreferenceClicked(preferenceName: String) {
        when (preferenceName) {
            TransactionPreferenceType.VALUE.uiName -> transactionListener.onValueEdit()
            TransactionPreferenceType.TYPE.uiName -> transactionListener.onTypeEdit()
            TransactionPreferenceType.CATEGORY.uiName -> transactionListener.onCategoryEdit()
        }
    }

    private fun getPreferencesList(): List<Any> {
        val value = StringUtils.formatMoney(
            StringUtils.convertMoneyForDisplay(
                transaction?.value ?: 0
            )
        ) + " " + (transaction?.currency ?: getString(R.string.wallet_rub))
        return listOf(
            ListTitle(getString(R.string.basic)),
            TransactionPreference(
                TransactionPreferenceType.VALUE,
                value,
                true
            ),
            TransactionPreference(
                TransactionPreferenceType.TYPE,
                transaction?.type ?: "",
                true
            ),
            TransactionPreference(
                TransactionPreferenceType.CATEGORY,
                categoryName ?: "",
                true
            ),
            ListTitle(getString(R.string.additional)),
            TransactionPreference(
                TransactionPreferenceType.DATE,
                transaction?.dateTime ?: DateTimeUtils.getCurrentDate(),
                false
            )
        )
    }

}