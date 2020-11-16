package com.example.loopdeck.ui.playlistrecnts

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loopdeck.utils.FileUtils
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import com.xorbix.loopdeck.cameraapp.BitmapUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class PlaylistRecntsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val playlistName = MutableLiveData<String?>()
    val recentsMediaList = MutableLiveData<List<File>>()

    private var mResultsBitmap: Bitmap? = null

    var importedFilesIntent: Intent? = null

    fun loadRecentList(context: Context) {
        val rootDir = FileUtils.getPlaylistDirectory(context , playlistName.value)
        recentsMediaList.value = rootDir.listFiles().toList()
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
                    BitmapUtils.saveImage(context, mResultsBitmap!!, playlistName.value)
                }
            }
        }

        importedFilesIntent?.data?.let {

            uriToMediaFile(context, it)?.let { file ->


                if (file.isVideo()) {
                    viewModelScope.launch(Dispatchers.IO) {
                        BitmapUtils.SaveVideo(context, it)
                    }
                    Toast.makeText(context, "Video Saved", Toast.LENGTH_SHORT).show()
                    return
                } else if (file.isImage()) {
                    try {

                        mResultsBitmap =
                            MediaStore.Images.Media.getBitmap(context.contentResolver, it)

                        viewModelScope.launch(Dispatchers.IO) {
                            BitmapUtils.saveImage(context, mResultsBitmap!!)
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
        try
        {
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
        }
        catch (e: Exception)
        {
            Toast.makeText(context, "Please select multiples images", Toast.LENGTH_LONG).show()
        }

        return null
    }


}