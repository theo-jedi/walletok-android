package com.theost.walletok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.theost.walletok.data.models.TransactionCategoryType
import com.theost.walletok.databinding.FragmentTransactionTypeBinding
import com.theost.walletok.delegates.TypeAdapterDelegate
import com.theost.walletok.delegates.TypeItem
import com.theost.walletok.widgets.TransactionTypeListener

class TransactionTypeFragment : Fragment() {

    companion object {
        private const val TRANSACTION_TYPE_KEY = "transaction_type"

        fun newFragment(savedType: String?): Fragment {
            val fragment = TransactionTypeFragment()
            val bundle = Bundle()
            bundle.putString(TRANSACTION_TYPE_KEY, savedType)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentTransactionTypeBinding
    private lateinit var savedType: String

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

        savedType = arguments?.getString(TRANSACTION_TYPE_KEY) ?: ""
        if (savedType != "") binding.submitButton.isEnabled = true

        val adapter = BaseAdapter()
        adapter.addDelegate(TypeAdapterDelegate { onItemClicked(it) })

        binding.listTypes.setHasFixedSize(true)
        binding.listTypes.adapter = adapter

        val typeItems = TransactionCategoryType.values().map { item ->
            TypeItem(
                name = item.uiName,
                isSelected = savedType == item.uiName
            )
        }
        adapter.setData(typeItems)

        binding.submitButton.setOnClickListener {
            setCurrentType()
        }

        return binding.root
    }

    private fun onItemClicked(type: String) {
        savedType = type
        if (savedType != "") {
            binding.submitButton.isEnabled = true
        }
    }

    private fun setCurrentType() {
        (activity as TransactionTypeListener).onTypeSubmitted(savedType)
    }

}