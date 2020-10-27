package com.xorbix.loopdeck.gallery.view

import com.xorbix.loopdeck.gallery.model.GalleryAlbums
import kotlin.collections.ArrayList

interface OnPhoneImagesObtained {
    fun onComplete(albums: ArrayList<GalleryAlbums>)
    fun onError()
}
