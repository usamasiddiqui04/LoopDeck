package com.example.loopdeck.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import com.example.loopdeck.utils.FileUtils.uriToMediaFile
import com.example.loopdeck.utils.extensions.getMediaType
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import com.picker.gallery.model.GalleryData
import com.xorbix.loopdeck.cameraapp.BitmapUtils
import java.io.File
import java.util.*

class MediaRepository(private val mediaDao: MediaDao, private val context: Context) {


    fun getAllRecentsMediaLiveData(): LiveData<List<MediaData>> = mediaDao.findRecents()

    fun getPlaylistMediaLiveData(playlistName: String): LiveData<List<MediaData>> =
        mediaDao.findByPlaylistLiveData(playlistName)

    fun addMediaOrPlaylist(file: File, playlistName: String? = null) {


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

    suspend fun addMedia(uri: Uri, playlistName: String? = null) {

        uriToMediaFile(context, uri)?.let { file ->
            var mResultsBitmap: Bitmap? = null

            when {
                file.isVideo() -> {
                    BitmapUtils.SaveVideo(context, uri)?.let {
                        addMediaOrPlaylist(it, playlistName)
                    }
                }

                file.isImage() -> {
                    mResultsBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

                    BitmapUtils.saveImage(context, mResultsBitmap)?.let {
                        addMediaOrPlaylist(it, playlistName)
                    }
                }
                else -> {
                }
            }
        }
    }


    suspend fun addMedia(data: GalleryData, playlistName: String? = null) {


        var mResultsBitmap: Bitmap? = null

        when {
            data.isVideo() -> {
                BitmapUtils.SaveVideo(context, Uri.fromFile(data.file))?.let {
                    addMediaOrPlaylist(it, playlistName)
                }
            }

            data.isImage() -> {
                mResultsBitmap = MediaStore.Images.Media.getBitmap(
                    context.contentResolver,
                    Uri.fromFile(data.file)
                )

                BitmapUtils.saveImage(context, mResultsBitmap)?.let {
                    addMediaOrPlaylist(it, playlistName)
                }
            }
            else -> {
            }
        }

    }

    suspend fun insertAndUpdateMediaToPlaylist(mediaDataList: List<MediaData>) {
        mediaDao.insert(mediaDataList)
    }

    suspend fun updatePlaylist(mediaDataList: List<MediaData>) {
        mediaDao.insert(mediaDataList)
    }

    suspend fun deleteMedia(mediaData: MediaData) {
        BitmapUtils.deleteImageFile(context, mediaData.filePath)
        mediaDao.delete(mediaData)
    }

    suspend fun deletePlaylist(playlistName: String) {
        mediaDao.deletePlaylist(playlistName)
    }
}


