package com.xorbics.loopdeck.gallery.view

import android.content.Context
import com.xorbics.loopdeck.gallery.model.GalleryAlbums
import com.xorbics.loopdeck.gallery.model.GalleryData
import kotlin.collections.ArrayList

interface ImagePickerContract {
    fun initRecyclerViews()
    fun galleryOperation()
    fun toggleDropdown()
    fun getPhoneAlbums(context: Context, listener: OnPhoneImagesObtained)
    fun updateTitle(galleryAlbums: GalleryAlbums = GalleryAlbums())
    fun updateSelectedPhotos(selectedlist: ArrayList<GalleryData> = ArrayList())
}