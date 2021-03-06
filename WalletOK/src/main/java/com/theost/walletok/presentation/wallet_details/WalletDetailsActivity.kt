package com.theost.walletok.presentation.wallet_details

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.theost.walletok.R
import com.theost.walletok.presentation.wallet_details.transaction.TransactionActivity
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.databinding.ActivityWalletDetailsBinding
import com.theost.walletok.presentation.SwipeControllerActions
import com.theost.walletok.presentation.WalletDetailsSwipeController
import com.theost.walletok.presentation.base.*
import com.theost.walletok.presentation.base.delegates.EmptyListAdapterDelegate
import com.theost.walletok.presentation.base.delegates.LoadingOrErrorAdapterDelegate
import com.theost.walletok.presentation.wallet_details.delegates.DateAdapterDelegate
import com.theost.walletok.presentation.wallet_details.delegates.TransactionAdapterDelegate
import com.theost.walletok.presentation.wallet_details.delegates.WalletDetailsHeaderAdapterDelegate
import com.theost.walletok.utils.Resource


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
                viewModel.loadNextPage()
        })
        binding.loadingBar.visibility = View.VISIBLE
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
        viewModel.allData.observe(this) {
            binding.loadingBar.visibility = View.GONE
            binding.emptyList.emptyLayout.visibility = if (it.transactions.isNotEmpty()) View.GONE else View.VISIBLE

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
                val transaction =
                    viewModel.allData.value!!.transactions.find { it.id == transactionId }
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

    override fun onStart() {
        super.onStart()
        viewModel.loadData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_wallet_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        showErrorToast()
        return super.onOptionsItemSelected(item)
    }

    private val transactionHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == RESULT_OK) {
                viewModel.loadData()
            }
        }

    private fun showErrorToast() {
        Toast.makeText(this, getString(R.string.not_available), Toast.LENGTH_SHORT).show()
    }

    private fun setAdapterData() {
        val data = viewModel.allData.value
        val paginationStatus = viewModel.paginationStatus.value
        if (data != null && paginationStatus != null) {
            walletDetailsAdapter.setData(
                TransactionItemsHelper.getRecyclerItems(
                    data.categories,
                    data.transactions,
                    data.wallets.find { it.id == walletId }!!,
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