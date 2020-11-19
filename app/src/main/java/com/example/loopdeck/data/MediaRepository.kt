package com.example.loopdeck.data

import androidx.lifecycle.LiveData
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import java.io.File
import java.util.*

class MediaRepository(private val mediaDao: MediaDao) {


    fun getAllRecentsMediaLiveData(): LiveData<List<MediaData>> = mediaDao.findRecents()

    fun getPlaylistMediaLiveData(playlistName: String): LiveData<List<MediaData>> =
        mediaDao.findByPlaylistLiveData(playlistName)

    suspend fun addMediaOrPlaylist(file: File, playlistName: String? = null) {

        val mediaCount = playlistName?.let { mediaDao.findByPlaylist(it).size } ?: -1

        val timestamp = Date()

        val mediaType = file.getMediaType()

        val dbPlaylistName = if (mediaType == MediaType.PLAYLIST) null else playlistName

        mediaDao.insert(
            MediaData(
                id = 0,
                name = file.name,
                extension = file.extension,
                filePath = file.absolutePath,
                createdAt = timestamp,
                modifiedAt = timestamp,
                playListName = dbPlaylistName,
                sequence = mediaCount + 1,
                mediaType = mediaType
            )
        )
    }

    suspend fun insertAndUpdateMediaToPlaylist(mediaData: List<MediaData>) {
        mediaDao.insert(mediaData)
    }

    suspend fun deleteMedia(mediaData: MediaData) {
        mediaDao.delete(mediaData)
    }

    suspend fun deletePlaylist(playlistName: String) {
        mediaDao.deletePlaylist(playlistName)
    }
}

private fun File.getMediaType(): String {
    return if (isImage()) MediaType.IMAGE else if (isVideo()) MediaType.VIDEO else MediaType.PLAYLIST
}
