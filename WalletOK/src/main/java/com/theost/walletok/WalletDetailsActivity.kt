package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.theost.walletok.databinding.ActivityWalletDetailsBinding
import com.theost.walletok.delegates.*
import java.util.*


class WalletDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletDetailsBinding

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, WalletDetailsActivity::class.java)
        }
    }

    private val data by lazy(LazyThreadSafetyMode.NONE) {
        listOf(
            HeaderContent(
                walletName = getString(R.string.wallet_name_textview),
                walletMoney = getString(R.string.amount_of_money_textview),
                walletGain = getString(R.string.amount_of_money_textview),
                walletLose = getString(R.string.amount_of_money_textview),
                walletLoseLimit = getString(R.string.wallet_lose_limit_text_view)
            )
        ).plus(TransactionItemsHelper.getTransactionItems())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val walletDetailsAdapter = BaseAdapter()
        walletDetailsAdapter.apply {
            addDelegate(WalletDetailsHeaderAdapterDelegate())
            addDelegate(DateAdapterDelegate())
            addDelegate(TransactionAdapterDelegate { /* TODO */ })
            addDelegate(EmptyListAdapterDelegate())
        }
        walletDetailsAdapter.setData(data)
        binding.recycler.apply {
            adapter = walletDetailsAdapter
            layoutManager = LinearLayoutManager(this@WalletDetailsActivity)
            setHasFixedSize(true)
        }
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.addOperationBtn.setOnClickListener {
            Toast.makeText(this, getString(R.string.button_clicked_toast), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_wallet_details, menu)
        return true
    }
}