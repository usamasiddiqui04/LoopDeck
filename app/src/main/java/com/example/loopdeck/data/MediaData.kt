package com.example.loopdeck.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "MediaFileTable")
data class MediaData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val filePath: String,
    val name: String,
    val extension: String? = null,
    val sequence: Int,
    val mediaType: String,
    val createdAt: Date?,
    val modifiedAt: Date?,
    val playListName: String? = null //Foriegn Lkey,
)

object MediaType {
    val IMAGE = "image"
    val VIDEO = "video"
    val PLAYLIST = "playlist"
}
