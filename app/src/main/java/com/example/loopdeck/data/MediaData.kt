package com.example.loopdeck.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.loopdeck.gallery.model.GalleryData
import com.example.loopdeck.utils.extensions.getMediaType
import java.util.*

@Entity(tableName = "MediaFileTable")
data class MediaData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val filePath: String,
    val name: String,
    val extension: String? = null,
    var sequence: Int,
    val mediaType: String,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    val source: String = MediaSource.PHONE_GALLERY,
    var thumbnail: String? = null,
    val playListName: String? = null //Foriegn Lkey,
) {
}

object MediaType {
    val IMAGE = "image"
    val VIDEO = "video"
    val PLAYLIST = "playlist"
}


object MediaSource {
    val GOOGLE_DRIVE = "google_drive"
    val ONE_DRIVE = "one_drive"
    val PHONE_GALLERY = "PHONE_gallery"
}


fun MediaData.isPlaylist(): Boolean {
    return this.playListName != null
}


fun GalleryData.toMediaData() {
    MediaData(
        id = 0,
        filePath = this.photoUri,
        name = this.name,
        extension = file.extension,
        sequence = 0,
        mediaType = file.getMediaType(),
        thumbnail = this.thumbnail
    )
}