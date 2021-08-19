package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.theost.walletok.models.Transaction
import com.theost.walletok.widgets.TransactionListener

class TransactionActivity : FragmentActivity(), TransactionListener {

    val transaction = Transaction("", "", "")

    companion object {
        const val TRANSACTION_DATA_KEY = "transaction_data"

        fun newIntent(context: Context): Intent {
            return Intent(context, TransactionActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation)

        if (savedInstanceState == null) {
            startTransactionValueFragment()
        }
    }

    override fun onSetValue(value: String) {
        transaction.value = value
        if (transaction.isFilled()) {
            startTransactionEditFragment()
        } else {
            startTransactionTypeFragment()
        }
    }

    override fun onSetType(type: String) {
        transaction.type = type
        if (transaction.isFilled()) {
            startTransactionEditFragment()
        } else {
            startTransactionCategoryFragment()
        }
    }

    override fun onSetCategory(category: String) {
        transaction.category = category
        startTransactionEditFragment()
    }

    override fun onEditValue() {
        startTransactionValueFragment()
    }

    override fun onEditType() {
        startTransactionTypeFragment()
    }

    override fun onEditCategory() {
        startTransactionCategoryFragment()
    }

    override fun onCreateTransaction() {
        // todo send to main
        println(transaction.value)
        println(transaction.type)
        println(transaction.category)
        finish()
    }

    private fun startTransactionValueFragment() {
        startFragment(TransactionValueFragment.newFragment(), transaction.value)
    }

    private fun startTransactionTypeFragment() {
        startFragment(TransactionTypeFragment.newFragment(), transaction.type)
    }

    private fun startTransactionCategoryFragment() {
        startFragment(TransactionCategoryFragment.newFragment(), transaction.category)
    }

    private fun startTransactionEditFragment() {
        startFragment(TransactionEditFragment.newFragment(), "")
    }

    private fun startFragment(fragment: Fragment, data: String) {
        val bundle = Bundle()
        bundle.putString(TRANSACTION_DATA_KEY, data)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.creation_fragment_container, fragment)
            .commitAllowingStateLoss()
    }

}