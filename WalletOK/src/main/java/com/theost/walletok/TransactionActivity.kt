package com.theost.walletok

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class TransactionActivity : FragmentActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, TransactionActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_creation)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.creation_fragment_container, TransactionValueFragment())
                .commitAllowingStateLoss()
        }
    }

}