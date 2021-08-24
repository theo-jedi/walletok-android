package com.theost.walletok.presentation.wallets.wallet_creation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.theost.walletok.databinding.ActivityWalletCreationBinding

class WalletCreationActivity : AppCompatActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, WalletCreationActivity::class.java)
        }
    }
    private lateinit var binding: ActivityWalletCreationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletCreationBinding.inflate(layoutInflater)
    }
}