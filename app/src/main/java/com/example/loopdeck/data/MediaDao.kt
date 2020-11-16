package com.example.loopdeck.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface MediaDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addMediaFiles (mediaData: MediaData)

    @Query("SELECT * FROM MediaFileTable ORDER BY id ASC")
    fun readAllMediaFiles() : LiveData<List<MediaData>>
}