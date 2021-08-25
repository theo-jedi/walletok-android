package com.theost.walletok.presentation.wallets.wallet_creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.theost.walletok.R
import com.theost.walletok.databinding.FragmentWalletLimitBinding

class WalletLimitFragment : Fragment() {
    companion object {
        const val FRAGMENT_TAG = "wallet_limit"
        fun newInstance(): Fragment {
            return WalletLimitFragment()
        }
    }

    private var _binding: FragmentWalletLimitBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WalletCreationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletLimitBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        binding.inputValue.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                viewModel.walletCreationModel.balanceLimit = null
            } else {
                viewModel.walletCreationModel.balanceLimit = it.toString().toLong()
            }
        }
        binding.submitButton.setOnClickListener {
            if (container != null) {
                activity?.onBackPressed()
            }
        }
        return binding.root
    }
}