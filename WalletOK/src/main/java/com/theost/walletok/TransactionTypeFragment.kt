package com.theost.walletok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.theost.walletok.databinding.FragmentTransactionTypeBinding
import com.theost.walletok.widgets.TransactionTypeAdapter
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

    private val savedType: String?
        get() = arguments?.getString(TRANSACTION_TYPE_KEY)

    private var lastSelected = -1

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

        val types = listOf("Доходы", "Расходы")
        binding.listTypes.adapter = TransactionTypeAdapter(types, savedType.orEmpty()) {
            onItemClicked(it)
        }

        return binding.root
    }

    private fun onItemClicked(position: Int) {
        if (lastSelected != position) {
            binding.submitButton.isEnabled = true
            if (lastSelected != -1) {
                binding.listTypes.layoutManager?.findViewByPosition(lastSelected)
                    ?.findViewById<ImageView>(R.id.type_check)?.visibility = View.INVISIBLE
            }
            lastSelected = position
        }
    }

    private fun setCurrentType() {
        val type = (binding.listTypes.adapter as TransactionTypeAdapter).getItem(lastSelected)
        (activity as TransactionTypeListener).onTypeSubmitted(type)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

}