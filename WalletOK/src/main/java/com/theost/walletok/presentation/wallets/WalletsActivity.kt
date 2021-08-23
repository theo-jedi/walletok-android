package com.theost.walletok.presentation.wallets

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.theost.walletok.databinding.ActivityWalletsBinding

class WalletsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWalletsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}