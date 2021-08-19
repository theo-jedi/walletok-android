package com.theost.walletok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.theost.walletok.databinding.FragmentTransactionTypeBinding
import com.theost.walletok.utils.ViewUtils
import com.theost.walletok.widgets.TransactionTypeAdapter

class TransactionTypeFragment : Fragment() {

    private var _binding: FragmentTransactionTypeBinding? = null
    private val binding get() = _binding!!
    private var lastSelected = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionTypeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            nextFragment()
        }

        val types = listOf("Доходы", "Расходы")
        binding.listTypes.adapter = TransactionTypeAdapter(types) {
            onItemClicked(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onItemClicked(position: Int) {
        if (lastSelected != position) {
            ViewUtils.enableSubmitButton(requireContext(), binding.submitButton)
            if (lastSelected != -1) {
                binding.listTypes.layoutManager?.findViewByPosition(lastSelected)
                    ?.findViewById<ImageView>(R.id.type_check)?.visibility = View.INVISIBLE
            }
            lastSelected = position
        }
    }

    private fun nextFragment() {
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.replace(R.id.creation_fragment_container, TransactionCategoryFragment())
        transaction?.disallowAddToBackStack()
        transaction?.commit()
    }

}