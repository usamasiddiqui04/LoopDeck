package com.xorbix.loopdeck.gallery.view

import android.content.Context
import com.xorbix.loopdeck.gallery.model.GalleryAlbums
import com.xorbix.loopdeck.gallery.model.GalleryData
import kotlin.collections.ArrayList

interface ImagePickerContract {
    fun initRecyclerViews()
    fun galleryOperation()
    fun toggleDropdown()
    fun getPhoneAlbums(context: Context, listener: OnPhoneImagesObtained)
    fun updateTitle(galleryAlbums: GalleryAlbums = GalleryAlbums())
    fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData> = ArrayList())
}