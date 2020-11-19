package com.example.loopdeck.ui.collection

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaDatabase
import com.example.loopdeck.data.MediaRepository
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import com.xorbix.loopdeck.cameraapp.BitmapUtils.SaveVideo
import com.xorbix.loopdeck.cameraapp.BitmapUtils.saveImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class CollectionViewModel(application: Application) : AndroidViewModel(application) {

    var importedFilesIntent: Intent? = null


    lateinit var recentsMediaLiveData: LiveData<List<MediaData>>
    lateinit var playListMediaLiveData: LiveData<List<MediaData>>

    private val repository: MediaRepository

    init {
        val mediaDao = MediaDatabase.getDatabase(application).mediaDao()
        repository = MediaRepository(mediaDao)

        getRecents()
    }

    private fun getRecents() {

        viewModelScope.launch {
            recentsMediaLiveData = repository.getAllRecentsMediaLiveData()
        }
    }


    fun getPlaylistMedia(playlistName: String) =
        repository.getPlaylistMediaLiveData(playlistName)


    fun importMediaFiles(context: Context, playlistName: String? = null) {
        importedFilesIntent?.clipData?.let {

            for (i in 0 until it.itemCount) {

                var mResultsBitmap: Bitmap? = null

                val imageuri = it.getItemAt(i).uri
                try {
                    if (imageuri != null) {
                        mResultsBitmap =
                            MediaStore.Images.Media.getBitmap(context.contentResolver, imageuri)
                    }

                } catch (e: Exception) {
                    //handle exception
                }

                viewModelScope.async(Dispatchers.IO) {
                    val file = saveImage(context, mResultsBitmap!!, playlistName)

                    if (file != null) {
                        repository.addMediaOrPlaylist(file, playlistName)
                    }

                }
            }
        } ?: run {


            importedFilesIntent?.data?.let {

                uriToMediaFile(context, it)?.let { file ->
                    var mResultsBitmap: Bitmap? = null

                    if (file.isVideo()) {
                        viewModelScope.launch(Dispatchers.IO) {
                            val file = SaveVideo(context, it)
                            if (file != null) {
                                repository.addMediaOrPlaylist(file, playlistName)
                            }
                        }
                        Toast.makeText(context, "Video Saved", Toast.LENGTH_SHORT).show()
                        return
                    } else if (file.isImage()) {
                        try {

                            mResultsBitmap =
                                MediaStore.Images.Media.getBitmap(context.contentResolver, it)

                            viewModelScope.launch(Dispatchers.IO) {
                                val file = saveImage(context, mResultsBitmap!!)
                                if (file != null) {
                                    repository.addMediaOrPlaylist(file, playlistName)
                                }
                            }
                        } catch (e: Exception) {
                            //handle exception
                        }
                        Toast.makeText(context, "Image Save", Toast.LENGTH_LONG).show()
                    }
                }

            }
        }


    }

    private fun uriToMediaFile(context: Context, uri: Uri): File? {
        try {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val filePath = cursor.getString(columnIndex)
                    cursor.close()
                    return File(filePath)
                }
                cursor.close()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Please select multiples images", Toast.LENGTH_LONG).show()
        }

        return null
    }

    fun createPlaylist(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMediaOrPlaylist(file)
        }
    }
}