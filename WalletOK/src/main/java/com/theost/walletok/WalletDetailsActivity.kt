package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
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
        walletDetailsAdapter.setData(TransactionItemsHelper.getData())
        binding.recycler.apply {
            adapter = walletDetailsAdapter
            layoutManager = LinearLayoutManager(this@WalletDetailsActivity)
            setHasFixedSize(true)
        }
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.addTransactionBtn.setOnClickListener {
            editTransaction(R.string.new_transaction)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_wallet_details, menu)
        return true
    }

    private fun editTransaction(mode: Int) {
        val intent = TransactionActivity.newIntent(this, mode)
        startActivity(intent)
    }

}