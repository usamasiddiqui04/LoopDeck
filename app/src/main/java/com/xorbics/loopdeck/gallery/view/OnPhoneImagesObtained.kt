package com.xorbics.loopdeck.gallery.view

import com.xorbics.loopdeck.gallery.model.GalleryAlbums
import kotlin.collections.ArrayList

interface OnPhoneImagesObtained {
    fun onComplete(albums: ArrayList<GalleryAlbums>)
    fun onError()
}
