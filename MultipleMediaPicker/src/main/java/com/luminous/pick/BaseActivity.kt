package com.luminous.pick

import androidx.appcompat.app.AppCompatActivity
import com.luminous.pick.utils.Utils
import com.nostra13.universalimageloader.core.ImageLoader

/**
 * Created by Kartum Infotech (Bhavesh Hirpara) on 09-Jul-18.
 */
open class BaseActivity : AppCompatActivity() {
    open var imageLoader: ImageLoader? = null
    open fun initImageLoader() {
        try {
            imageLoader = Utils.initImageLoader(activity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val activity: BaseActivity
        get() = this
}