package com.theost.walletok.presentation.wallets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.theost.walletok.R
import com.theost.walletok.databinding.ActivityWalletsBinding
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.presentation.base.delegates.EmptyListAdapterDelegate
import com.theost.walletok.presentation.wallet_details.WalletDetailsActivity
import com.theost.walletok.presentation.wallets.delegates.WalletItemDelegate
import com.theost.walletok.presentation.wallets.delegates.WalletsCurrenciesDelegate
import com.theost.walletok.presentation.wallets.delegates.WalletsHeaderDelegate
import com.theost.walletok.presentation.wallets.wallet_creation.WalletCreationActivity
import com.theost.walletok.utils.Resource
import com.theost.walletok.utils.ViewUtils

class WalletsActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, WalletsActivity::class.java)
        }
    }

    private val viewModel: WalletsViewModel by viewModels()
    private lateinit var binding: ActivityWalletsBinding
    private val walletsAdapter = BaseAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        walletsAdapter.apply {
            addDelegate(WalletsHeaderDelegate())
            addDelegate(WalletsCurrenciesDelegate())
            addDelegate(WalletItemDelegate {
                startActivity(WalletDetailsActivity.newIntent(this@WalletsActivity, it.id))
            })
            addDelegate(EmptyListAdapterDelegate())
        }
        binding.recycler.apply {
            adapter = walletsAdapter
            setHasFixedSize(true)
        }
        binding.errorWidget.retryButton.setOnClickListener {
            viewModel.loadData()
            ViewUtils.hideErrorMessage(binding.errorWidget.errorLayout)
        }
        binding.loadingBar.visibility = View.VISIBLE
        binding.createWalletBtn.visibility = View.GONE
        binding.createWalletBtn.setOnClickListener {
            startActivity(WalletCreationActivity.newIntent(this))
        }
        viewModel.loadingStatus.observe(this) {
            //if (it is Resource.Error) ViewUtils.showErrorMessage(binding.errorWidget.errorLayout)
        }
        viewModel.walletsAndOverall.observe(this) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            val tapedValue = TypedValue()
            theme.resolveAttribute(R.attr.colorPrimary, tapedValue, true)
            window.statusBarColor = tapedValue.data

            binding.loadingBar.visibility = View.GONE
            binding.createWalletBtn.visibility = View.VISIBLE

            walletsAdapter.setData(
                WalletsItemHelper.getData(
                    wallets = it.wallets,
                    walletsOverall = it.walletsOverall,
                    currenciesPrices = viewModel.currenciesPrices.value
                )
            )
        }
        viewModel.currenciesPrices.observe(this) {
            val oldData = viewModel.walletsAndOverall.value
            if (oldData != null)
                walletsAdapter.setData(
                    WalletsItemHelper.getData(
                        wallets = oldData.wallets,
                        walletsOverall = oldData.walletsOverall,
                        currenciesPrices = viewModel.currenciesPrices.value
                    )
                )
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData()
    }
}