package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.repositories.TransactionsRepository
import com.theost.walletok.databinding.ActivityWalletDetailsBinding
import com.theost.walletok.delegates.*
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.*


class WalletDetailsActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, WalletDetailsActivity::class.java)
        }
    }

    private lateinit var binding: ActivityWalletDetailsBinding
    private lateinit var walletDetailsAdapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        walletDetailsAdapter = BaseAdapter()
        walletDetailsAdapter.apply {
            addDelegate(WalletDetailsHeaderAdapterDelegate())
            addDelegate(DateAdapterDelegate())
            addDelegate(TransactionAdapterDelegate())
            addDelegate(EmptyListAdapterDelegate())
        }

        TransactionItemsHelper.getData().subscribeOn(AndroidSchedulers.mainThread()).doOnSuccess {
            walletDetailsAdapter.setData(it)
        }.subscribe()

        binding.recycler.apply {
            adapter = walletDetailsAdapter
            layoutManager = LinearLayoutManager(this@WalletDetailsActivity)
            setHasFixedSize(true)
        }
        binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.addTransactionBtn.setOnClickListener { createTransaction() }
        val swipeController = WalletDetailsSwipeController(this, object : SwipeControllerActions {
            override fun onDeleteClicked(viewHolder: RecyclerView.ViewHolder) {
                DeleteTransactionDialogFragment.newInstance {
                    TransactionsRepository.removeTransaction((viewHolder as TransactionAdapterDelegate.ViewHolder).transactionId)
                        .doOnComplete {
                            TransactionItemsHelper.getData()
                                .subscribeOn(AndroidSchedulers.mainThread()).doOnSuccess {
                                    walletDetailsAdapter.setData(it)
                                }.subscribe()
                        }.subscribe()
                }.show(supportFragmentManager, "dialog")
            }

            override fun onEditClicked(viewHolder: RecyclerView.ViewHolder) {
                val transactionId = (viewHolder as TransactionAdapterDelegate.ViewHolder).transactionId
                TransactionsRepository.getTransactions()
                    .subscribeOn(AndroidSchedulers.mainThread()).doOnSuccess { it ->
                        val transaction = it.find { it.id == transactionId }
                        if (transaction != null) editTransaction(transaction)
                    }.subscribe()
            }

        })
        ItemTouchHelper(swipeController)
            .attachToRecyclerView(binding.recycler)

        binding.recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                swipeController.onDraw(c)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_wallet_details, menu)
        return true
    }

    private val transactionHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == RESULT_OK) {
                TransactionItemsHelper.getData()
                    .subscribeOn(AndroidSchedulers.mainThread()).doOnSuccess {
                        walletDetailsAdapter.setData(it)
                    }.subscribe()
            }
        }

    private fun createTransaction() {
        transactionHandler.launch(TransactionActivity.newIntent(this, null))
    }

    private fun editTransaction(transaction: Transaction) {
        transactionHandler.launch(TransactionActivity.newIntent(this, transaction))
    }

}