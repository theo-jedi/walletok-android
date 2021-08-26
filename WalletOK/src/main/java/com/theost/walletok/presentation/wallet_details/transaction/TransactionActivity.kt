package com.theost.walletok.presentation.wallet_details.transaction

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.theost.walletok.R
import com.theost.walletok.data.models.CategoryCreationModel
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.databinding.ActivityTransactionBinding
import com.theost.walletok.presentation.wallet_details.category.CategoryDeleteFragment
import com.theost.walletok.presentation.wallet_details.category.CategoryEditFragment
import com.theost.walletok.presentation.wallet_details.category.CategoryNameFragment
import com.theost.walletok.presentation.wallet_details.category.CategoryTypeFragment
import com.theost.walletok.presentation.wallet_details.transaction.widgets.*
import com.theost.walletok.utils.Resource
import com.theost.walletok.presentation.wallet_details.transaction.widgets.CategoryIconListener
import com.theost.walletok.presentation.wallet_details.transaction.widgets.CategoryListener
import com.theost.walletok.presentation.wallet_details.transaction.widgets.CategoryNameListener
import com.theost.walletok.presentation.wallet_details.transaction.widgets.CategoryTypeListener
import io.reactivex.disposables.CompositeDisposable
import java.util.*

class TransactionActivity : FragmentActivity(),
    TransactionListener, TransactionValueListener, TransactionTypeListener,
    TransactionCategoryListener, CategoryTypeListener, CategoryListener,
    CategoryNameListener, CategoryIconListener, TransactionDateListener {

    companion object {
        private const val WALLET_ID_KEY = "wallet_id"
        private const val TRANSACTION_KEY = "transaction"
        private const val TRANSACTION_TITLE_KEY = "transaction_titleRes"

        fun newIntent(
            context: Context,
            transaction: Transaction?,
            titleRes: Int,
            walletId: Int
        ): Intent {
            val intent = Intent(context, TransactionActivity::class.java)
            intent.putExtra(WALLET_ID_KEY, walletId)
            intent.putExtra(TRANSACTION_KEY, transaction)
            intent.putExtra(TRANSACTION_TITLE_KEY, titleRes)
            return intent
        }
    }

    private lateinit var binding: ActivityTransactionBinding

    private val savedTransaction: Transaction?
        get() = intent.getParcelableExtra(TRANSACTION_KEY)
    private val titleRes: Int
        get() = intent.getIntExtra(TRANSACTION_TITLE_KEY, R.string.new_transaction)

    private var transactionModel = TransactionCreationModel()
    private var categoryModel: CategoryCreationModel? = null

    private val viewModel: TransactionViewModel by viewModels()
    private val compositeDisposable = CompositeDisposable()
    private val walletId: Int
        get() = intent.extras!!.getInt(WALLET_ID_KEY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.loadingStatus.observe(this) { onObserveLoading(it) }
        viewModel.sendingStatus.observe(this) { onObserveSending(it) }

        binding.errorWidget.retryButton.setOnClickListener {
            viewModel.loadData(savedTransaction!!)
        }

        binding.errorWidget.closeButton.setOnClickListener {
            binding.errorWidget.errorLayout.visibility = View.GONE
        }

        binding.closeButton.setOnClickListener { onBackPressed() }

        viewModel.allData.observe(this) { transaction ->
            binding.transactionProgress.visibility = View.GONE
            transactionModel = transaction
            startFragment(TransactionEditFragment.newFragment(transaction, R.string.edit_transaction))
        }

        if (savedInstanceState == null) {
            if (savedTransaction != null) {
                binding.transactionProgress.visibility = View.VISIBLE
                viewModel.loadData(savedTransaction!!)
            } else {
                startFragment(TransactionValueFragment.newFragment())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    override fun onBackPressed() {

        val currentFragment =
            supportFragmentManager.findFragmentById(R.id.creation_fragment_container)
        if (transactionModel.isFilled() && currentFragment !is TransactionEditFragment) {
            startFragment(TransactionEditFragment.newFragment(transactionModel, titleRes))
        } else if (currentFragment is CategoryNameFragment || currentFragment is CategoryTypeFragment ) {
            supportFragmentManager.popBackStack()
            startFragment(CategoryEditFragment.newFragment(categoryModel))
        } else {
            if (currentFragment is TransactionValueFragment || currentFragment is TransactionEditFragment) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            super.onBackPressed()
        }
    }

    override fun onValueEdit() {
        startFragment(TransactionValueFragment.newFragment(transactionModel.value ?: 0))
    }

    override fun onTypeEdit() {
        startFragment(TransactionTypeFragment.newFragment(transactionModel.type))
    }

    override fun onCategoryEdit() {
        startFragment(
            TransactionCategoryFragment.newFragment(
                transactionModel.category,
                transactionModel.type
            )
        )
    }

    override fun onValueSubmitted(value: Long) {
        viewModel.setValue(value)
        transactionModel.value = value
        if (transactionModel.isFilled()) {
            startFragment(TransactionEditFragment.newFragment(transactionModel, titleRes))
        } else {
            startFragment(TransactionTypeFragment.newFragment(transactionModel.type))
        }
    }

    override fun onTypeSubmitted(type: String) {
        if (transactionModel.isFilled() && transactionModel.type == type) {
            startFragment(TransactionEditFragment.newFragment(transactionModel, titleRes))
        } else {
            viewModel.setType(type)
            transactionModel.type = type
            transactionModel.category = null
            startFragment(
                TransactionCategoryFragment.newFragment(
                    transactionModel.category,
                    transactionModel.type
                )
            )
        }
    }

    override fun onCategorySubmitted(category: Int) {
        viewModel.setCategory(category)
        transactionModel.category = category
        startFragment(TransactionEditFragment.newFragment(transactionModel, titleRes))
    }

    override fun onDateSubmitted(dateTime: Date) {
        viewModel.setDateTime(dateTime)
       transactionModel.dateTime = dateTime
    }

    override fun onTransactionSubmitted() {
        if (transactionModel.isFilled()) {
            viewModel.sendData(transactionModel, walletId)
        }
    }

    override fun onCreateCategoryClicked() {
        categoryModel = CategoryCreationModel()
        categoryModel!!.type = transactionModel.type
        startFragment(CategoryEditFragment.newFragment(categoryModel))
    }

    override fun onDeleteCategoryClicked() {
        startFragment(CategoryDeleteFragment.newFragment())
    }

    override fun onCategoryNameEdit() {
        supportFragmentManager.popBackStack()
        startFragment(CategoryNameFragment.newFragment(categoryModel?.name))
    }

    override fun onCategoryTypeEdit() {
        supportFragmentManager.popBackStack()
        startFragment(CategoryTypeFragment.newFragment(categoryModel?.type))
    }

    override fun onCategoryNameSubmitted(name: String) {
        categoryModel?.name = name
        supportFragmentManager.popBackStack()
        startFragment(CategoryEditFragment.newFragment(categoryModel))
    }

    override fun onCategoryTypeSubmitted(type: String) {
        categoryModel?.type = type
        supportFragmentManager.popBackStack()
        startFragment(CategoryEditFragment.newFragment(categoryModel))
    }

    override fun onCategoryColorSubmitted(color: Int) {
        categoryModel?.color = color
    }

    override fun onCategoryIconSubmitted(iconRes: Int) {
        categoryModel?.iconRes = iconRes
    }

    override fun onCategoryCreated() {
        categoryModel = null
        supportFragmentManager.popBackStack()
    }

    override fun onCategoryDeleted() {
        supportFragmentManager.popBackStack()
    }

    private fun onObserveLoading(status: Resource<*>) {
        if (status is Resource.Error) {
            binding.errorWidget.errorLayout.visibility = View.VISIBLE
            binding.errorWidget.retryButton.visibility = View.VISIBLE
            binding.errorWidget.closeButton.visibility = View.GONE
        } else if (status is Resource.Success) {
            binding.errorWidget.errorLayout.visibility = View.GONE
        }
    }

    private fun onObserveSending(status: Resource<*>) {
        binding.transactionProgress.visibility = if (status is Resource.Loading) View.VISIBLE else View.GONE
        if (status is Resource.Error) {
            binding.errorWidget.errorLayout.visibility = View.VISIBLE
            binding.errorWidget.retryButton.visibility = View.GONE
            binding.errorWidget.closeButton.visibility = View.VISIBLE
        } else if (status is Resource.Success) {
            binding.errorWidget.errorLayout.visibility = View.GONE
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(R.id.creation_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}