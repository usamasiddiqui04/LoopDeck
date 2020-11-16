package com.example.loopdeck.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MediaFileTable")
data class MediaData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val file_path: String,
    val name: String,
    val playListId: String?
)