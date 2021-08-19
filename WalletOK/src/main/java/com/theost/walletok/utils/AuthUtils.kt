package com.theost.walletok.utils

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

class AuthUtils {

    companion object {
        fun getLastSignedInAccount(context: Context): GoogleSignInAccount? {
            return GoogleSignIn.getLastSignedInAccount(context)
        }
    }

}