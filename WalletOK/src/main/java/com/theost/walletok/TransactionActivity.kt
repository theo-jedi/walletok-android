package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.theost.walletok.models.Transaction
import com.theost.walletok.widgets.TransactionListener

class TransactionActivity : FragmentActivity(), TransactionListener {

    val transaction = Transaction("", "", "", "")

    companion object {
        const val TRANSACTION_VALUE_KEY = "transaction_value"
        const val TRANSACTION_TYPE_KEY = "transaction_type"
        const val TRANSACTION_CATEGORY_KEY = "transaction_category"

        fun newIntent(context: Context): Intent {
            return Intent(context, TransactionActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation)

        if (savedInstanceState == null) {
            startFragment(TransactionValueFragment.newFragment())
        }
    }

    override fun onCreateTransaction() {
        // todo send to main
        println(transaction.value)
        println(transaction.type)
        println(transaction.category)
        finish()
    }

    override fun onSetTransactionData(data: String, key: String) {
        when (key) {
            TRANSACTION_VALUE_KEY -> transaction.value = data
            TRANSACTION_TYPE_KEY -> transaction.type = data
            TRANSACTION_CATEGORY_KEY -> transaction.category = data
        }

        if (!transaction.isFilled()) {
            when (key) {
                TRANSACTION_VALUE_KEY -> startFragment(TransactionTypeFragment.newFragment())
                TRANSACTION_TYPE_KEY -> startFragment(TransactionCategoryFragment.newFragment())
                TRANSACTION_CATEGORY_KEY -> startFragment(TransactionEditFragment.newFragment())
            }
        } else {
            startFragment(TransactionEditFragment.newFragment())
        }
    }

    override fun onEditTransactionData(key: String) {
        when (key) {
            TRANSACTION_VALUE_KEY -> startBundleFragment(TransactionValueFragment.newFragment(), transaction.value, key)
            TRANSACTION_TYPE_KEY -> startBundleFragment(TransactionTypeFragment.newFragment(), transaction.type, key)
            TRANSACTION_CATEGORY_KEY -> startBundleFragment(TransactionCategoryFragment.newFragment(), transaction.category, key)
        }
    }

    private fun startFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.creation_fragment_container, fragment)
            .commitAllowingStateLoss()
    }

    private fun startBundleFragment(fragment: Fragment, data: String, key: String) {
        val bundle = Bundle()
        bundle.putString(key, data)
        fragment.arguments = bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.creation_fragment_container, fragment)
            .commitAllowingStateLoss()
    }

}