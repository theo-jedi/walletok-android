package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.theost.walletok.data.models.Transaction
import com.theost.walletok.data.models.TransactionCategory
import com.theost.walletok.data.models.TransactionCreationModel
import com.theost.walletok.data.repositories.CategoriesRepository
import com.theost.walletok.data.repositories.TransactionsRepository
import com.theost.walletok.databinding.ActivityTransactionBinding
import com.theost.walletok.widgets.TransactionCategoryListener
import com.theost.walletok.widgets.TransactionListener
import com.theost.walletok.widgets.TransactionTypeListener
import com.theost.walletok.widgets.TransactionValueListener
import io.reactivex.android.schedulers.AndroidSchedulers

class TransactionActivity : FragmentActivity(), TransactionListener, TransactionValueListener, TransactionTypeListener,
    TransactionCategoryListener {

    companion object {
        private const val TRANSACTION_KEY = "transaction_edit_mode"

        fun newIntent(context: Context, transaction: Transaction?): Intent {
            val intent = Intent(context, TransactionActivity::class.java)
            intent.putExtra(TRANSACTION_KEY, transaction)
            return intent
        }
    }

    private lateinit var binding: ActivityTransactionBinding
    private val transaction = TransactionCreationModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val savedTransaction = intent.getParcelableExtra<Transaction>(TRANSACTION_KEY)
        if (savedInstanceState == null) {
            if (savedTransaction != null) {
                restoreSavedTransaction(savedTransaction)
            } else {
                startFragment(TransactionValueFragment.newFragment(null))
            }
        }
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.creation_fragment_container)
        if (transaction.isFilled() && currentFragment !is TransactionEditFragment) {
            startFragment(TransactionEditFragment.newFragment(transaction))
        } else if (currentFragment  is TransactionCategoryFragment) {
            startFragment(TransactionTypeFragment.newFragment(transaction.type))
        } else {
            if (currentFragment is TransactionValueFragment || currentFragment is TransactionEditFragment) {
                supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
            super.onBackPressed()
        }
    }

    private fun restoreSavedTransaction(savedTransaction: Transaction) {
        binding.transactionProgress.visibility = View.VISIBLE
        CategoriesRepository.getCategories().subscribeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { it ->
                val category = it.find { it.id == savedTransaction.categoryId }!!
                loadSavedTransaction(savedTransaction, category)
                binding.transactionProgress.visibility = View.GONE
                startFragment(TransactionEditFragment.newFragment(transaction))
            }.subscribe()
    }

    private fun loadSavedTransaction(savedTransaction: Transaction, savedCategory: TransactionCategory) {
        transaction.id = savedTransaction.id
        transaction.value = savedTransaction.money
        transaction.type = savedCategory.type.uiName
        transaction.category = savedTransaction.categoryId
        transaction.currency = savedTransaction.currency
        transaction.dateTime = savedTransaction.dateTime
    }

    override fun onValueEdit() {
        startFragment(TransactionValueFragment.newFragment(transaction.value))
    }

    override fun onTypeEdit() {
        startFragment(TransactionTypeFragment.newFragment(transaction.type))
    }

    override fun onCategoryEdit() {
        startFragment(TransactionCategoryFragment.newFragment(transaction.category, transaction.type))
    }

    override fun onValueSubmitted(value: Int) {
        transaction.value = value
        if (transaction.isFilled()) {
            startFragment(TransactionEditFragment.newFragment(transaction))
        } else {
            startFragment(TransactionTypeFragment.newFragment(transaction.type))
        }
    }

    override fun onTypeSubmitted(type: String) {
        if (transaction.isFilled() && transaction.type == type) {
            startFragment(TransactionEditFragment.newFragment(transaction))
        } else {
            transaction.type = type
            transaction.category = null
            startFragment(TransactionCategoryFragment.newFragment(transaction.category, transaction.type))
        }
    }

    override fun onCategorySubmitted(category: Int) {
        transaction.category = category
        startFragment(TransactionEditFragment.newFragment(transaction))
    }

    override fun onTransactionSubmitted() {
        if (transaction.isFilled()) {
            if (transaction.id != null) {
                TransactionsRepository.editTransaction(transaction.id!!, transaction.value!!, transaction.category!!)
            } else {
                TransactionsRepository.addTransaction(transaction.value!!, transaction.category!!)
            }
            setResult(RESULT_OK)
        }
        finish()
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.creation_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

}