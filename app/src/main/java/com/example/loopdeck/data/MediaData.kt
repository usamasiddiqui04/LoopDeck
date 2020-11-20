package com.example.loopdeck.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.loopdeck.utils.extensions.getMediaType
import com.picker.gallery.model.GalleryData
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
    val playListName: String? = null //Foriegn Lkey,
) {
}

object MediaType {
    val IMAGE = "image"
    val VIDEO = "video"
    val PLAYLIST = "playlist"
}


fun GalleryData.toMediaData() {
    MediaData(
        id = 0,
        filePath = this.photoUri,
        name = this.name,
        extension = file.extension,
        sequence = 0,
        mediaType = file.getMediaType()
    )
}