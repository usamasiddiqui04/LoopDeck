package com.xorbix.loopdeck.imageloader

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


class GlideImageLoader constructor(private val context: Context) : ImageLoader {

    override fun loadImage(url: String, target: ImageView, @DrawableRes placeholderDrawable: Int, @DrawableRes errorDrawable: Int) {
        val requestOptions = RequestOptions()
                .placeholder(placeholderDrawable)
                .error(errorDrawable)

        Glide.with(context)
                .setDefaultRequestOptions(requestOptions)
                .load(url)
                .into(target)
    }
}