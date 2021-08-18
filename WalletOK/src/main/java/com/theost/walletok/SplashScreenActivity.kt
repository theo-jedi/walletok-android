package com.theost.walletok
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
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
            val intent = AuthActivity.newIntent(this)
            startActivity(intent)
        } else {
            onSignedIn(account)
        }
    }

    private fun onSignedIn(account: GoogleSignInAccount?) {
        // todo start next activity
    }
}
