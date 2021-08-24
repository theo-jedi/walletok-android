package com.theost.walletok.presentation.wallet_details

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.*
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.databinding.ActivityWalletDetailsBinding
import com.theost.walletok.delegates.*
import com.theost.walletok.presentation.SwipeControllerActions
import com.theost.walletok.presentation.WalletDetailsSwipeController
import com.theost.walletok.presentation.base.*
import com.theost.walletok.presentation.base.delegates.EmptyListAdapterDelegate
import com.theost.walletok.presentation.base.delegates.LoadingOrErrorAdapterDelegate
import com.theost.walletok.presentation.wallet_details.delegates.*
import com.theost.walletok.presentation.wallet_details.transaction.TransactionActivity
import com.theost.walletok.utils.Resource
import java.util.*


class WalletDetailsActivity : AppCompatActivity() {

    companion object {
        private const val WALLET_ID_KEY = "wallet_id"
        fun newIntent(context: Context, walletId: Int): Intent {
            val intent = Intent(context, WalletDetailsActivity::class.java)
            intent.putExtra(WALLET_ID_KEY, walletId)
            return intent
        }
    }

    private val walletId: Int
        get() = intent.extras!!.getInt(WALLET_ID_KEY)
    private lateinit var binding: ActivityWalletDetailsBinding
    private lateinit var walletDetailsAdapter: BaseAdapter
    private val viewModel: WalletDetailsViewModel by viewModels(factoryProducer = {
        WalletDetailsViewModel.Factory(walletId)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        walletDetailsAdapter = PaginationAdapter(PaginationAdapterHelper {
            if (viewModel.paginationStatus.value == PaginationStatus.Ready)
                viewModel.loadData()
        })
        viewModel.paginationStatus.observe(this) {
            setAdapterData()
        }
        walletDetailsAdapter.apply {
            addDelegate(WalletDetailsHeaderAdapterDelegate())
            addDelegate(DateAdapterDelegate())
            addDelegate(TransactionAdapterDelegate())
            addDelegate(EmptyListAdapterDelegate())
            addDelegate(LoadingOrErrorAdapterDelegate {
                viewModel.loadData()
            })
        }
        viewModel.loadData()
        viewModel.allData.observe(this) {
            setAdapterData()
        }

        viewModel.removeTransactionStatus.observe(this) {
            when (it) {
                is Resource.Success -> viewModel.loadData()
                is Resource.Error -> {
                    ErrorMessageHelper.setUpErrorMessage(binding.errorWidget) {
                    }
                }
                is Resource.Loading -> {
                }
            }
        }

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
                    if (viewHolder is TransactionAdapterDelegate.ViewHolder)
                        viewModel.removeTransaction(viewHolder.transactionId)
                }.show(supportFragmentManager, "dialog")
            }

            override fun onEditClicked(viewHolder: RecyclerView.ViewHolder) {
                val transactionId =
                    (viewHolder as TransactionAdapterDelegate.ViewHolder).transactionId
                val transaction = viewModel.allData.value!!.second.find { it.id == transactionId }
                if (transaction != null) editTransaction(transaction)
            }

        })
        ItemTouchHelper(swipeController)
            .attachToRecyclerView(binding.recycler)

        binding.recycler.addItemDecoration(
            object : RecyclerView.ItemDecoration() {
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
                viewModel.loadData()
            }
        }

    private fun setAdapterData() {
        val data = viewModel.allData.value
        val paginationStatus = viewModel.paginationStatus.value
        if (data != null && paginationStatus != null) {
            val (categories, transactions, wallet) = data
            walletDetailsAdapter.setData(
                TransactionItemsHelper.getRecyclerItems(
                    categories,
                    transactions,
                    wallet,
                    paginationStatus
                )
            )
        }
    }

    private fun createTransaction() {
        transactionHandler.launch(
            TransactionActivity.newIntent(
                this,
                null,
                R.string.new_transaction,
                walletId
            )
        )
    }

    private fun editTransaction(transaction: Transaction) {
        transactionHandler.launch(
            TransactionActivity.newIntent(
                this,
                transaction,
                R.string.edit_transaction,
                walletId
            )
        )
    }

}