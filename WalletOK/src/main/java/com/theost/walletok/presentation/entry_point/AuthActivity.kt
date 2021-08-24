package com.theost.walletok.presentation.entry_point

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.theost.walletok.R
import com.theost.walletok.presentation.wallet_details.WalletDetailsActivity
import com.theost.walletok.presentation.wallets.WalletsActivity


class AuthActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AuthActivity::class.java)
        }
    }

    private val authorizationHandler =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->

            if (result?.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.result

                onSignedIn(account)
            } else {
                showErrorToast()
            }

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        findViewById<AppCompatButton>(R.id.auth_button).setOnClickListener {
            authorizationHandler.launch(getSignInIntent())
        }
    }

    private fun showErrorToast() {
        Toast.makeText(this, getString(R.string.error_sign_in), Toast.LENGTH_SHORT).show()
    }

    private fun getSignInIntent(): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        return mGoogleSignInClient.signInIntent
    }

    private fun onSignedIn(account: GoogleSignInAccount?) {
        val intent = WalletsActivity.newIntent(this)
        startActivity(intent)
        finish()
    }

}