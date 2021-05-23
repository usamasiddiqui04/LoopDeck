package com.xorbics.loopdeck.utils.extensions

import android.content.res.Resources


val Int.dp: Int
    get() = Math.round(this * Resources.getSystem().displayMetrics.density)
val Int.px: Int
    get() = Math.round(this / Resources.getSystem().displayMetrics.density)