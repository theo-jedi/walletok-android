package com.theost.walletok.presentation.wallet_details.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import com.theost.walletok.R
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.databinding.FragmentTransactionEditBinding
import com.theost.walletok.delegates.*
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionDateListener
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionListener
import com.theost.walletok.utils.DateTimeUtils
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.StringUtils
import java.util.*

class TransactionEditFragment : Fragment() {

    companion object {
        private const val TRANSACTION_MODEL_KEY = "transaction_model"
        private const val TRANSACTION_TITLE_KEY = "transaction_title"

        fun newFragment(transaction: TransactionCreationModel, titleRes: Int): Fragment {
            val fragment = TransactionEditFragment()
            val bundle = Bundle()
            bundle.putParcelable(TRANSACTION_MODEL_KEY, transaction)
            bundle.putInt(TRANSACTION_TITLE_KEY, titleRes)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var _binding: FragmentTransactionEditBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentDate: Calendar
    private lateinit var preferencesList: List<Any>

    private var categoryName: String? = null
    private var transaction: TransactionCreationModel? = null

    private val adapter = BaseAdapter()
    private val viewModel: TransactionCreationViewModel by viewModels()

    private val titleRes: Int
        get() = arguments?.getInt(TRANSACTION_TITLE_KEY)!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionEditBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.title = getString(titleRes)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        adapter.apply {
            addDelegate(PreferenceAdapterDelegate { onPreferenceClicked(it) })
            addDelegate(TitleAdapterDelegate())
        }

        viewModel.allData.observe(viewLifecycleOwner) { pair ->
            transaction = pair.first
            categoryName = pair.second
            preferencesList = getPreferencesList()

            binding.submitButton.isEnabled = transaction!!.isFilled()

            adapter.setData(preferencesList)
        }

        viewModel.loadingStatus.observe(viewLifecycleOwner) {
            binding.errorWidget.errorLayout.visibility = if (it is Resource.Error) View.VISIBLE else View.GONE
            binding.transactionProgress.visibility = if (it is Resource.Loading) View.VISIBLE else View.GONE
        }

        binding.listPreferences.setHasFixedSize(true)
        binding.listPreferences.adapter = adapter

        binding.errorWidget.retryButton.setOnClickListener {
            viewModel.loadData(transaction!!)
        }

        binding.submitButton.setOnClickListener {
            onTransactionSubmitted()
        }

        viewModel.loadData(transaction!!)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        transaction = if (savedInstanceState == null) {
            arguments?.getParcelable(TRANSACTION_MODEL_KEY)
        } else {
            savedInstanceState.getParcelable(TRANSACTION_MODEL_KEY)
        }

        currentDate = Calendar.getInstance()
        if (transaction?.dateTime != null) {
            currentDate.time = transaction!!.dateTime!!
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(TRANSACTION_MODEL_KEY, transaction)
        super.onSaveInstanceState(outState)
    }

    private fun onPreferenceClicked(preferenceName: String) {
        when (preferenceName) {
            PreferenceType.VALUE.uiName -> (activity as TransactionListener).onValueEdit()
            PreferenceType.TYPE.uiName -> (activity as TransactionListener).onTypeEdit()
            PreferenceType.CATEGORY.uiName -> (activity as TransactionListener).onCategoryEdit()
            PreferenceType.DATE.uiName -> pickDate()
        }
    }

    private fun pickDate() {
        val datePicker = MaterialDatePicker.Builder.datePicker().build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val date = Date()
            date.time = selection
            currentDate.time = date
            pickTime()
        }
        datePicker.show(activity?.supportFragmentManager!!, "date_picker")
    }

    private fun pickTime() {
        val timePicker = MaterialTimePicker.Builder().setTimeFormat(CLOCK_24H).build()
        timePicker.addOnPositiveButtonClickListener {
            currentDate.set(Calendar.HOUR_OF_DAY, timePicker.hour)
            currentDate.set(Calendar.MINUTE, timePicker.minute)
            onDateSubmitted()
        }
        timePicker.show(activity?.supportFragmentManager!!, "time_picker")
    }

    private fun onDateSubmitted() {
        (activity as TransactionDateListener).onDateSubmitted(currentDate.time)
        viewModel.setDate(currentDate.time)
    }

    private fun onTransactionSubmitted() {
        if (transaction?.dateTime == null) (activity as TransactionDateListener).onDateSubmitted(Date())
        (activity as TransactionListener).onTransactionSubmitted()
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
                PreferenceType.VALUE,
                value,
                true
            ),
            TransactionPreference(
                PreferenceType.TYPE,
                transaction?.type ?: getString(R.string.unset),
                true
            ),
            TransactionPreference(
                PreferenceType.CATEGORY,
                categoryName ?: getString(R.string.unset),
                true
            ),
            ListTitle(getString(R.string.additional)),
            TransactionPreference(
                PreferenceType.DATE, if (transaction!!.dateTime != null)
                    DateTimeUtils.getFormattedDateOrCurrent(transaction!!.dateTime!!)
                else getString(R.string.now),
                true
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}