package com.theost.walletok.presentation.wallets.wallet_creation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.theost.walletok.R
import com.theost.walletok.databinding.FragmentWalletEditBinding
import com.theost.walletok.delegates.PreferenceAdapterDelegate
import com.theost.walletok.delegates.PreferenceType
import com.theost.walletok.delegates.TransactionPreference
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.ViewUtils

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
    private val adapter = BaseAdapter()

    private lateinit var container: ViewGroup

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWalletEditBinding.inflate(inflater, container, false)
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        if (container != null) {
            this.container = container
        }
        binding.submitButton.setOnClickListener {
            viewModel.walletCreationModel.balanceLimit = 10
            viewModel.addWallet()
        }
        binding.errorWidget.closeButton.setOnClickListener {
            ViewUtils.hideErrorMessage(binding.errorWidget.errorLayout)
        }
        viewModel.addWalletStatus.observe(viewLifecycleOwner) {
            if (it is Resource.Error) ViewUtils.showErrorMessage(binding.errorWidget.errorLayout)
            if (it is Resource.Success) activity?.finish()
        }
        if (container != null) {
            adapter.addDelegate(PreferenceAdapterDelegate {
                when (it) {
                    PreferenceType.NAME.uiName -> startFragment(WalletNameFragment.newInstance())
                    PreferenceType.CURRENCY.uiName -> startFragment(WalletCurrencyFragment.newInstance())
                    PreferenceType.LIMIT.uiName -> startFragment(WalletLimitFragment.newInstance())
                }
            })
        }
        binding.recyclerWalletProperties.adapter = adapter
        adapter.setData(getPreferencesList())
        return binding.root
    }

    private fun getPreferencesList(): List<TransactionPreference> {
        val list = mutableListOf(
            TransactionPreference(
                PreferenceType.NAME,
                viewModel.walletCreationModel.name, true
            ),
            TransactionPreference(
                PreferenceType.CURRENCY, viewModel.walletCreationModel.currency!!.shortName, true
            )
        )
        return list
    }

    private fun startFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(container.id, fragment)
            .addToBackStack(null)
            .commit()
    }

}