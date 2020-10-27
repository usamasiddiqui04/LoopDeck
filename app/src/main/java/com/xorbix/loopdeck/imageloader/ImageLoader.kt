package com.xorbix.loopdeck.imageloader

import android.widget.ImageView
import androidx.annotation.DrawableRes


interface ImageLoader {

    fun loadImage(url: String, target: ImageView, @DrawableRes placeholderDrawable: Int, @DrawableRes errorDrawable: Int)
}