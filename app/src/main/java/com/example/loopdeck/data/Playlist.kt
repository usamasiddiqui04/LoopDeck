package com.example.loopdeck.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.loopdeck.gallery.model.GalleryData
import com.example.loopdeck.utils.extensions.getMediaType
import java.util.*

@Entity(tableName = "PlaylistTable")
data class Playlist(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val filePath: String,
    val name: String,
    val mediaType: String,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    var thumbnail: String? = null,

)