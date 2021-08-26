package com.theost.walletok.presentation.wallets.wallet_creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.theost.walletok.databinding.FragmentWalletEditBinding
import com.theost.walletok.utils.Resource

class WalletEditFragment : Fragment() {
    companion object {
        const val FRAGMENT_TAG = "wallet_edit"
        fun newInstance(): Fragment {
            return WalletEditFragment()
        }
    }

    private var _binding: FragmentWalletEditBinding? = null
    private val binding get() = _binding!!
    private val viewModel: WalletCreationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletEditBinding.inflate(inflater, container, false)
        binding.submitButton.setOnClickListener {
            viewModel.addWallet()
        }
        viewModel.addWalletStatus.observe(viewLifecycleOwner) {
            if (it is Resource.Success) activity?.finish()
        }
        return binding.root
    }
}