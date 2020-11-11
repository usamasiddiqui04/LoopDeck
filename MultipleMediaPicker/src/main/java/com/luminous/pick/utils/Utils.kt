package com.luminous.pick.utils

import android.content.Context
import android.graphics.Bitmap
import com.luminous.pick.Action
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.utils.L
import com.nostra13.universalimageloader.utils.StorageUtils

/**
 * Created by Kartum Infotech (Bhavesh Hirpara) on 09-Jul-18.
 */
object Utils {
    fun initImageLoader(mContext: Context?): ImageLoader? {
        var imageLoader: ImageLoader? = null
        try {
            val cacheDir = StorageUtils.getOwnCacheDirectory(
                mContext,
                Action.CACHE_DIR
            )
            val defaultOptions = DisplayImageOptions.Builder()
                .cacheOnDisk(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).build()
            val builder = ImageLoaderConfiguration.Builder(
                mContext
            ).defaultDisplayImageOptions(defaultOptions)
                .diskCache(UnlimitedDiskCache(cacheDir))
                .memoryCache(WeakMemoryCache())
            val config = builder.build()
            imageLoader = ImageLoader.getInstance()
            imageLoader.init(config)
            L.writeLogs(false)
            return imageLoader
        } catch (e: Exception) {
            sendExceptionReport(e)
        }
        try {
            val defaultOptions = DisplayImageOptions.Builder()
                .cacheOnDisk(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).build()
            val builder = ImageLoaderConfiguration.Builder(
                mContext
            ).defaultDisplayImageOptions(defaultOptions)
                .memoryCache(WeakMemoryCache())
            val config = builder.build()
            imageLoader = ImageLoader.getInstance()
            imageLoader.init(config)
            L.writeLogs(false)
        } catch (e: Exception) {
            sendExceptionReport(e)
        }
        return imageLoader
    }

    fun sendExceptionReport(e: Exception) {
        e.printStackTrace()
        try {
            // Writer result = new StringWriter();
            // PrintWriter printWriter = new PrintWriter(result);
            // e.printStackTrace(printWriter);
            // String stacktrace = result.toString();
            // new CustomExceptionHandler(c, URLs.URL_STACKTRACE)
            // .sendToServer(stacktrace);
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }
}