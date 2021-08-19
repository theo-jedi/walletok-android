package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.theost.walletok.databinding.ActivityWalletDetailsBinding
import com.theost.walletok.delegates.*
import java.text.SimpleDateFormat
import java.util.*


class WalletDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWalletDetailsBinding

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, WalletDetailsActivity::class.java)
        }
    }

    private val transactions by lazy(LazyThreadSafetyMode.NONE) {
        (0..9).map {
            TransactionContent(
                categoryName = getString(R.string.salary),
                iconResId = R.drawable.ic_round_card,
                time = getString(R.string.twelve_o_clock),
                date = "2021/08/1${it}",
                moneyAmount = getString(R.string.amount_of_money_textview),
                transactionType = getString(R.string.income_text_view)
            )
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
        ).plus(transactionItems(transactions))
    }

    private fun transactionItems(transactions: List<TransactionContent>): List<Any> {
        val result = mutableListOf<Any>()
        val currentLocale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            resources.configuration.locales[0]
        } else {
            resources.configuration.locale
        }
        val transactionDateFormat = SimpleDateFormat("yyyy/MM/dd", currentLocale)
        val dateItemDateFormat = SimpleDateFormat("dd MMMM", currentLocale)
        val calendar = Calendar.getInstance()
        val today = transactionDateFormat.format(calendar.time)
        calendar.add(Calendar.DATE, -1)
        val yesterday = transactionDateFormat.format(calendar.time)
        var lastItemDate = ""
        transactions.sortedByDescending { it.date }.forEach {
            if (it.date != lastItemDate) {
                result.add(
                    when (it.date) {
                        today -> getString(R.string.today)
                        yesterday -> getString(R.string.yesterday)
                        else -> dateItemDateFormat.format(transactionDateFormat.parse(it.date)!!)
                    }
                )
                lastItemDate = it.date
            }
            result.add(it)
        }
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        val walletDetailsAdapter = WalletDetailsAdapter()
        walletDetailsAdapter.apply {
            addDelegate(WalletDetailsHeaderAdapterDelegate())
            addDelegate(DateAdapterDelegate())
            addDelegate(TransactionAdapterDelegate())
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