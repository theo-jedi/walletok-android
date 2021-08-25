package com.theost.walletok

import android.app.Application
import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        svgImageLoader =
            ImageLoader.Builder(context).componentRegistry { add(SvgDecoder(context)) }.build()
    }

    companion object {
        lateinit var context: Context // TODO
        lateinit var svgImageLoader: ImageLoader
    }
}