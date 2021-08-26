package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.theost.walletok.data.models.CategoryCreationModel
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.data.repositories.TransactionsRepository
import com.theost.walletok.databinding.ActivityTransactionBinding
import com.theost.walletok.presentation.base.ErrorMessageHelper
import com.theost.walletok.presentation.wallet_details.category.CategoryDeleteFragment
import com.theost.walletok.presentation.wallet_details.category.CategoryEditFragment
import com.theost.walletok.presentation.wallet_details.category.CategoryNameFragment
import com.theost.walletok.presentation.wallet_details.category.CategoryTypeFragment
import com.theost.walletok.presentation.wallet_details.transaction.TransactionCategoryFragment
import com.theost.walletok.presentation.wallet_details.transaction.TransactionEditFragment
import com.theost.walletok.presentation.wallet_details.transaction.TransactionTypeFragment
import com.theost.walletok.presentation.wallet_details.transaction.TransactionValueFragment
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionCategoryListener
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionListener
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionTypeListener
import com.theost.walletok.presentation.wallet_details.transaction.widgets.TransactionValueListener
import com.theost.walletok.utils.addTo
import com.theost.walletok.widgets.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

class TransactionActivity : FragmentActivity(),
    TransactionListener, TransactionValueListener, TransactionTypeListener,
    TransactionCategoryListener, CategoryTypeListener, CategoryListener,
    CategoryNameListener, CategoryIconListener {

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

    private val transaction = TransactionCreationModel()
    private var categoryModel: CategoryCreationModel? = null

    private val compositeDisposable = CompositeDisposable()
    private val walletId: Int
        get() = intent.extras!!.getInt(WALLET_ID_KEY)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeButton.setOnClickListener { onBackPressed() }

        if (savedInstanceState == null) {
            if (savedTransaction != null) {
                restoreSavedTransaction(savedTransaction!!)
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
        val currentFragment = supportFragmentManager.findFragmentById(R.id.creation_fragment_container)
        if (transaction.isFilled() && currentFragment !is TransactionEditFragment) {
            startFragment(TransactionEditFragment.newFragment(transaction, titleRes))
        } else if (currentFragment is CategoryNameFragment || currentFragment is CategoryTypeFragment ) {
            supportFragmentManager.popBackStack()
            startFragment(CategoryEditFragment.newFragment(categoryModel!!))
        } else {
            if (currentFragment is TransactionValueFragment || currentFragment is TransactionEditFragment) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            super.onBackPressed()
        }
    }

    private fun restoreSavedTransaction(savedTransaction: Transaction) {
        binding.transactionProgress.visibility = View.VISIBLE
        binding.closeButton.visibility = View.INVISIBLE

        CategoriesRepository.getCategories().subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                val category = list.data!!.find { it.id == savedTransaction.categoryId }!!
                loadSavedTransaction(savedTransaction, category)
                binding.transactionProgress.visibility = View.GONE
                startFragment(TransactionEditFragment.newFragment(transaction, titleRes))
            }, {
                binding.closeButton.visibility = View.VISIBLE
                binding.transactionProgress.visibility = View.GONE
                ErrorMessageHelper.setUpErrorMessage(binding.errorWidget) {
                    restoreSavedTransaction(savedTransaction)
                }
            }).addTo(compositeDisposable)
    }

    private fun loadSavedTransaction(
        savedTransaction: Transaction,
        savedCategory: TransactionCategory
    ) {
        transaction.id = savedTransaction.id
        transaction.value = savedTransaction.money
        transaction.type = savedCategory.type.uiName
        transaction.category = savedTransaction.categoryId
        transaction.dateTime = savedTransaction.dateTime
    }

    override fun onValueEdit() {
        startFragment(TransactionValueFragment.newFragment(transaction.value ?: 0))
    }

    override fun onTypeEdit() {
        startFragment(TransactionTypeFragment.newFragment(transaction.type))
    }

    override fun onCategoryEdit() {
        startFragment(
            TransactionCategoryFragment.newFragment(
                transaction.category,
                transaction.type
            )
        )
    }

    override fun onValueSubmitted(value: Long) {
        transaction.value = value
        if (transaction.isFilled()) {
            startFragment(TransactionEditFragment.newFragment(transaction, titleRes))
        } else {
            startFragment(TransactionTypeFragment.newFragment(transaction.type))
        }
    }

    override fun onTypeSubmitted(type: String) {
        if (transaction.isFilled() && transaction.type == type) {
            startFragment(TransactionEditFragment.newFragment(transaction, titleRes))
        } else {
            transaction.type = type
            transaction.category = null
            startFragment(
                TransactionCategoryFragment.newFragment(
                    transaction.category,
                    transaction.type
                )
            )
        }
    }

    override fun onCreateCategoryClicked() {
        categoryModel = CategoryCreationModel()
        categoryModel!!.type = transaction.type
        startFragment(CategoryEditFragment.newFragment(categoryModel!!))
    }

    override fun onDeleteCategoryClicked() {
        startFragment(CategoryDeleteFragment.newFragment())
    }

    override fun onCategorySubmitted(category: Int) {
        transaction.category = category
        startFragment(TransactionEditFragment.newFragment(transaction, titleRes))
    }

    override fun onTransactionSubmitted() {
        if (transaction.isFilled()) {
            binding.transactionProgress.visibility = View.VISIBLE
            binding.closeButton.visibility = View.INVISIBLE
            if (transaction.id != null) {
                TransactionsRepository.editTransaction(
                    transaction.id!!,
                    transaction.value!!,
                    transaction.category!!,
                    walletId
                ).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        binding.transactionProgress.visibility = View.GONE
                        setResult(RESULT_OK)
                        finish()
                    }, {
                        binding.closeButton.visibility = View.VISIBLE
                        ErrorMessageHelper.setUpErrorMessage(binding.errorWidget) {
                            onTransactionSubmitted()
                        }
                    }).addTo(compositeDisposable)
            } else {
                TransactionsRepository.addTransaction(
                    walletId,
                    transaction.value!!,
                    transaction.category!!
                ).observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        binding.transactionProgress.visibility = View.GONE
                        setResult(RESULT_OK)
                        finish()
                    }, {
                        binding.closeButton.visibility = View.VISIBLE
                        ErrorMessageHelper.setUpErrorMessage(binding.errorWidget) {
                            onTransactionSubmitted()
                        }
                    }).addTo(compositeDisposable)
            }
        }
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
        startFragment(CategoryEditFragment.newFragment(categoryModel!!))
    }

    override fun onCategoryTypeSubmitted(type: String) {
        categoryModel?.type = type
        supportFragmentManager.popBackStack()
        startFragment(CategoryEditFragment.newFragment(categoryModel!!))
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