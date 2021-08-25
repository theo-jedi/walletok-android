package com.theost.walletok.presentation.entry_point

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.theost.walletok.presentation.wallet_details.WalletDetailsActivity
import com.theost.walletok.presentation.wallets.WalletsActivity
import com.theost.walletok.utils.AuthUtils


class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onLastSignedIn()
        finish()
    }

    private fun onLastSignedIn() {
        val account = AuthUtils.getLastSignedInAccount(this)
        if (account == null) {
            startAuthActivity()
        } else {
            onSignedIn(account)
        }
    }

    private fun startAuthActivity() {
        val intent = AuthActivity.newIntent(this)
        startActivity(intent)
    }

    private fun onSignedIn(account: GoogleSignInAccount?) {
        val intent = WalletsActivity.newIntent(this)
        startActivity(intent)
    }
}
