package com.example.loopdeck.ui.recents

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaDatabase
import com.example.loopdeck.data.MediaRepository
import com.example.loopdeck.utils.FileUtils
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import com.xorbix.loopdeck.cameraapp.BitmapUtils.SaveVideo
import com.xorbix.loopdeck.cameraapp.BitmapUtils.saveImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class RecentsViewModel(application: Application) : AndroidViewModel(application) {
    val playlistName = MutableLiveData<String?>()
    val recentsMediaList = MutableLiveData<List<File>>()
    private var mResultsBitmap: Bitmap? = null
    val file: File? = null

    val readAllData: LiveData<List<MediaData>>
    private val repository: MediaRepository

    init {
        val mediaDao = MediaDatabase.getDatabase(application).mediaDao()
        repository = MediaRepository(mediaDao)
        readAllData = repository.readAllData
    }

    fun addMedia(mediaData: MediaData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMediaFile(mediaData)
        }
    }

    var importedFilesIntent: Intent? = null

    fun loadRecentList(context: Context) {
        val rootDir = FileUtils.getRootDirectory(context)
        recentsMediaList.value = rootDir.listFiles().toList().sortedBy { it.lastModified() }
    }


    fun importMediaFiles(context: Context) {

        importedFilesIntent?.clipData?.let {

            for (i in 0 until it.itemCount) {
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
                    val file = saveImage(context, mResultsBitmap!!, playlistName.value)

                    if (file != null) {
                        addMedia(MediaData(0, file.absolutePath, file.name, playlistName.value))
                    }

                }
            }
        }

        importedFilesIntent?.data?.let {

            uriToMediaFile(context, it)?.let { file ->


                if (file.isVideo()) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val file = SaveVideo(context, it)
                        if (file != null) {
                            addMedia(MediaData(0, file.absolutePath, file.name, playlistName.value))
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
                                addMedia(
                                    MediaData(
                                        0,
                                        file.absolutePath,
                                        file.name,
                                        playlistName.value
                                    )
                                )
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
}