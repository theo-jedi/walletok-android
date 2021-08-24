package com.theost.walletok.presentation.wallets

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.theost.walletok.databinding.ActivityWalletsBinding
import com.theost.walletok.presentation.base.BaseAdapter
import com.theost.walletok.presentation.wallet_details.WalletDetailsActivity
import com.theost.walletok.presentation.wallets.delegates.WalletItemDelegate
import com.theost.walletok.presentation.wallets.delegates.WalletsCurrenciesDelegate
import com.theost.walletok.presentation.wallets.delegates.WalletsHeaderDelegate
import com.theost.walletok.utils.Resource

class WalletsActivity : AppCompatActivity() {
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
                val position = binding.recycler.getChildLayoutPosition(it)
                val data = viewModel.walletsAndOverall.value
                if (data != null) {
                    val walletId = data.wallets[position - WalletsItemHelper.walletsListOffset].id
                    startActivity(WalletDetailsActivity.newIntent(this@WalletsActivity, walletId))
                }
            })
        }
        binding.recycler.apply {
            adapter = walletsAdapter
            layoutManager = LinearLayoutManager(this@WalletsActivity)
            setHasFixedSize(true)
        }
        binding.errorWidget.retryButton.setOnClickListener {
            viewModel.loadData()
        }
        viewModel.loadingStatus.observe(this) {
            binding.errorWidget.errorLayout.visibility =
                if (it is Resource.Error) View.VISIBLE else View.GONE
        }
        viewModel.walletsAndOverall.observe(this) {
            walletsAdapter.setData(
                WalletsItemHelper.getData(
                    wallets = it.wallets,
                    walletsOverall = it.walletsOverall
                )
            )
        }
        viewModel.loadData()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, WalletsActivity::class.java)
        }
    }
}