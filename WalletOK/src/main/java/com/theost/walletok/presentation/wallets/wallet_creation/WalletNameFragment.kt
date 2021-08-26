package com.theost.walletok.presentation.wallets.wallet_creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.theost.walletok.R
import com.theost.walletok.databinding.FragmentWalletNameBinding

class WalletNameFragment : Fragment() {
    companion object {
        const val FRAGMENT_TAG = "wallet_name"
        fun newInstance(): Fragment {
            return WalletNameFragment()
        }
    }

    private var _binding: FragmentWalletNameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WalletCreationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletNameBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.inputValue.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                viewModel.walletCreationModel.name = ""
                binding.submitButton.isEnabled = false
            } else {
                viewModel.walletCreationModel.name = it.toString()
                binding.submitButton.isEnabled = true
            }
        }
        binding.submitButton.setOnClickListener {
            if (container != null) {
                parentFragmentManager.commit {
                    replace(container.id, WalletCurrencyFragment.newInstance())
                    setReorderingAllowed(true)
                    addToBackStack(FRAGMENT_TAG)
                }
            }
        }
        return binding.root
    }
}