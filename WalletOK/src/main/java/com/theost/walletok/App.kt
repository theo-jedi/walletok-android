package com.theost.walletok

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import coil.ImageLoader
import coil.decode.SvgDecoder
import com.theost.walletok.data.db.WalletOKDatabase
import java.util.concurrent.Executors

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        svgImageLoader =
            ImageLoader.Builder(context).componentRegistry { add(SvgDecoder(context)) }.build()
        appDatabase = Room.databaseBuilder(
            context,
            WalletOKDatabase::class.java, "app_database"
        ).setQueryCallback({ sqlQuery, bindArgs ->
            println("SQL Query: $sqlQuery SQL Args: $bindArgs")
        }, Executors.newSingleThreadExecutor())
            .build()
    }

    companion object {
        lateinit var context: Context // TODO
        lateinit var svgImageLoader: ImageLoader
        lateinit var appDatabase: WalletOKDatabase
    }
}