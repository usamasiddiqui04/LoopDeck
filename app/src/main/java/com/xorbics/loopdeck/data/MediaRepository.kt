package com.xorbics.loopdeck.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import com.xorbics.loopdeck.gallery.model.GalleryData
import com.xorbics.loopdeck.utils.extensions.getMediaType
import com.xorbics.loopdeck.utils.isImage
import com.xorbics.loopdeck.utils.isVideo
import com.xorbics.loopdeck.BitmapUtils
import java.io.*
import java.util.*

class MediaRepository(private val mediaDao: MediaDao, private val context: Context) {


    fun getAllRecentsMediaLiveData(): LiveData<List<MediaData>> = mediaDao.findRecents()

    fun getAllPlaylistMediaLiveData(): LiveData<List<MediaData>> = mediaDao.findAllPlaylist()

    fun getAllPublihsedMediaLiveData(): LiveData<List<PublishData>> = mediaDao.findPublish()

    fun getPlaylistMediaLiveData(playlistName: String): LiveData<List<MediaData>> =
        mediaDao.findByPlaylistLiveData(playlistName)

    fun getPlaylistImage(playlistName: String): LiveData<String> =
        mediaDao.findByPlaylistImage(playlistName)

    fun addPublishData(file: File) {
        val timestamp = Date()

        val mediaType = file.getMediaType()

        mediaDao.insertPublish(
            PublishData(
                id = 0,
                name = file.name,
                extension = file.extension,
                filePath = file.absolutePath,
                createdAt = timestamp,
                modifiedAt = timestamp,
                mediaType = mediaType
            )
        )

    }

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

    suspend fun addEditedFile(filepath: File, playlistName: String? = null) {
        if (filepath.isVideo()) {
            BitmapUtils.SaveVideo(context, Uri.fromFile(filepath), playlistName).let {
                addMediaOrPlaylist(it!!, playlistName)
            }
        } else if (filepath.isImage()) {
            var mResultsBitmap: Bitmap? = null
            mResultsBitmap =
                MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(filepath))
            BitmapUtils.saveImage(context, mResultsBitmap, playlistName).let {
                addMediaOrPlaylist(it!!, playlistName)
            }
        }
    }

    suspend fun addPublishedFileData(filepath: File) {
        if (filepath.isVideo()) {
            BitmapUtils.SaveVideo(context, Uri.fromFile(filepath)).let {
                addPublishData(it!!)
            }
        } else if (filepath.isImage()) {
            var mResultsBitmap: Bitmap? = null
            mResultsBitmap =
                MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(filepath))
            BitmapUtils.saveImage(context, mResultsBitmap).let {
                addPublishData(it!!)
            }
        }
    }


    suspend fun addDublicateMedia(filepath: File, playlistName: String? = null) {
        if (filepath.isVideo()) {
            BitmapUtils.SaveVideo(context, Uri.fromFile(filepath), playlistName).let {
                addMediaOrPlaylist(it!!, playlistName)
            }
        } else if (filepath.isImage()) {
            var mResultsBitmap: Bitmap? = null
            mResultsBitmap =
                MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(filepath))
            BitmapUtils.saveImage(context, mResultsBitmap, playlistName).let {
                addMediaOrPlaylist(it!!, playlistName)
            }
        }
    }

    @Throws(IOException::class)
    fun copy(src: File?, dst: File?) {
        val `in`: InputStream = FileInputStream(src)
        try {
            val out: OutputStream = FileOutputStream(dst)
            try {
                // Transfer bytes from in to out
                val buf = ByteArray(1024)
                var len: Int
                while (`in`.read(buf).also { len = it } > 0) {
                    out.write(buf, 0, len)
                }
            } finally {
                out.close()
            }
        } finally {
            `in`.close()
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


