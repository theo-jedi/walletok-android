package com.theost.walletok.presentation.wallet_details.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.theost.walletok.R
import com.theost.walletok.databinding.FragmentTransactionTypeBinding
import com.theost.walletok.delegates.TypeAdapterDelegate
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionTypeListener

class TransactionTypeFragment : Fragment() {

    companion object {
        private const val TRANSACTION_TYPE_KEY = "transaction_type"

        fun newFragment(savedType: String? = ""): Fragment {
            val fragment = TransactionTypeFragment()
            val bundle = Bundle()
            bundle.putString(TRANSACTION_TYPE_KEY, savedType)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentTransactionTypeBinding
    private lateinit var savedType: String

    private val viewModel: TransactionTypesViewModel by viewModels()
    private val adapter = BaseAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTransactionTypeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            setCurrentType()
        }

        adapter.addDelegate(TypeAdapterDelegate { position ->
            onItemClicked(position)
        })

        binding.listTypes.setHasFixedSize(true)
        binding.listTypes.adapter = adapter

        viewModel.allData.observe(viewLifecycleOwner) { list ->
            val typeItem = list.find { it.isSelected }
            if (typeItem != null) {
                savedType = typeItem.name
                binding.submitButton.isEnabled = true
            } else {
                binding.submitButton.isEnabled = false
            }

            adapter.setData(list)
        }

        viewModel.loadData(savedType)

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        savedType = if (savedInstanceState == null) {
            arguments?.getString(TRANSACTION_TYPE_KEY) ?: ""
        } else {
            savedInstanceState.getString(TRANSACTION_TYPE_KEY) ?: ""
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(TRANSACTION_TYPE_KEY, savedType)
        super.onSaveInstanceState(outState)
    }

    private fun onItemClicked(position: Int) {
        viewModel.selectData(position)
    }

    private fun setCurrentType() {
        (activity as TransactionTypeListener).onTypeSubmitted(savedType)
    }

}