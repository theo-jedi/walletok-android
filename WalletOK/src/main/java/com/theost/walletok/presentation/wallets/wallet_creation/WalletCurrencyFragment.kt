package com.theost.walletok.presentation.wallets.wallet_creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.theost.walletok.R
import com.theost.walletok.databinding.FragmentWalletCurrencyBinding
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.presentation.wallets.delegates.CurrencyItemDelegate

class WalletCurrencyFragment : Fragment() {
    companion object {
        const val FRAGMENT_TAG = "wallet_currency"
        fun newInstance(): Fragment {
            return WalletCurrencyFragment()
        }
    }

    private var _binding: FragmentWalletCurrencyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WalletCreationViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletCurrencyBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        val adapter = BaseAdapter()
        viewModel.loadCurrencies()
        viewModel.currencies.observe(viewLifecycleOwner) {
            adapter.setData(WalletCurrencyItemsHelper.getData(requireContext(), null, it))
        }
        adapter.addDelegate(CurrencyItemDelegate {
            viewModel.walletCreationModel.currency = it
            val currencies = viewModel.currencies.value
            if (currencies != null) {
                adapter.setData(WalletCurrencyItemsHelper.getData(requireContext(), it, currencies))
                binding.submitButton.isEnabled = true
            }
        })
        binding.recycler.adapter = adapter
        binding.submitButton.setOnClickListener {
            if (container != null) {
                parentFragmentManager.commit {
                    replace(container.id, WalletEditFragment.newInstance())
                    setReorderingAllowed(true)
                    addToBackStack(FRAGMENT_TAG)
                }
            }
        }
        return binding.root
    }
}