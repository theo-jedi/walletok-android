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
import com.theost.walletok.presentation.base.DiffAdapter
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
        val adapter = DiffAdapter()
        viewModel.loadCurrencies()
        viewModel.currencies.observe(viewLifecycleOwner) {
            adapter.submitList(WalletCurrencyItemsHelper.getData(requireContext(), null, it))
        }
        adapter.addDelegate(CurrencyItemDelegate {
            if (viewModel.walletCreationModel.currency != it || !binding.submitButton.isEnabled) {
                viewModel.walletCreationModel.currency = it
                val currencies = viewModel.currencies.value
                if (currencies != null) {
                    adapter.submitList(
                        WalletCurrencyItemsHelper.getData(
                            requireContext(),
                            it,
                            currencies
                        )
                    )
                    binding.submitButton.isEnabled = true
                }
            } else {
                binding.submitButton.isEnabled = false
            }
        })
        binding.recycler.adapter = adapter
        binding.submitButton.setOnClickListener {
            if (container != null) {
                parentFragmentManager.commit {
                    setCustomAnimations(
                    R.anim.fade_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.fade_out
                    )
                    replace(container.id, WalletEditFragment.newInstance())
                    setReorderingAllowed(true)
                    addToBackStack(FRAGMENT_TAG)
                }
            }
        }
        return binding.root
    }
}