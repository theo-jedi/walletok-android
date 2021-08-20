package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.data.repositories.TransactionsRepository
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
        val swipeController = WalletDetailsSwipeController(this, object : SwipeControllerActions {
            override fun onDeleteClicked(position: Int) {
                DeleteTransactionDialogFragment.newInstance {
                    TransactionsRepository.removeTransaction(position)
                    walletDetailsAdapter.setData(TransactionItemsHelper.getData())
                }.show(supportFragmentManager, "dialog")
            }

            override fun onEditClicked(position: Int) {

            }

        })
        ItemTouchHelper(swipeController)
            .attachToRecyclerView(binding.recycler)

        binding.recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
                super.onDraw(c, parent, state)
                parent.children.forEach { item ->
                    val position = parent.getChildLayoutPosition(item)
                    val viewHolderClass =
                        (parent.adapter as BaseAdapter).getDelegateClassByPos(position)
                    if (viewHolderClass == TransactionAdapterDelegate::class)
                        swipeController.onDraw(c)
                }
            }
        })
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