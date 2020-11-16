package com.example.loopdeck.data

import androidx.lifecycle.LiveData

class MediaRepository(private val mediaDao: MediaDao) {

    val readAllData : LiveData<List<MediaData>> = mediaDao.readAllMediaFiles()

    suspend fun addMediaFile(mediaData: MediaData){
        mediaDao.addMediaFiles(mediaData)
    }
}