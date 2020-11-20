package com.picker.gallery.model

import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.android.parcel.Parcelize
import java.io.File

@Parcelize
data class GalleryData(
    var id: Int = 0,
    var albumName: String = "",
    var photoUri: String = "",
    var name: String = "",
    var file: File,
    var albumId: Int = 0,
    var isSelected: Boolean = false,
    var isEnabled: Boolean = true,
    var mediaType: Int = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
    var duration: Int = 0,
    var dateAdded: String = "",
    var thumbnail: String = ""
) : Parcelable {

    fun isVideo() = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
    fun isImage() = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE

}