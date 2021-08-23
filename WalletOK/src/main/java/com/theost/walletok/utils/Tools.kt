package com.theost.walletok.utils

import android.content.Context
import android.util.TypedValue
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


fun dpToPx(dp: Float, context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    ).toInt()
}

fun Disposable.addTo(compositeDisposable: CompositeDisposable): Disposable =
    apply { compositeDisposable.add(this) }