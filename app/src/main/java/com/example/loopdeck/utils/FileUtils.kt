package com.example.loopdeck.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import com.example.loopdeck.utils.filters.ImageFileFilter
import com.example.loopdeck.utils.filters.VideoFileFilter
import java.io.File

object FileUtils {
    fun getRootDirectory(context: Context): File {
        return getOrCreateDirectory(context, "/Loopdeck Media Files")
    }

    fun getPlaylistDirectory(context: Context , path :String?): File {
        return getOrCreateDirectory(context, "/Loopdeck Media Files/$path")
    }


    fun getOrCreateDirectory(context: Context, fileName: String): File {
        val dir = File(context.getExternalFilesDir(null)!!.absolutePath, fileName)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }


     fun uriToMediaFile(context: Context, uri: Uri): File? {
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


fun File.isImage() = ImageFileFilter().accept(this)
fun File.isVideo() = VideoFileFilter().accept(this)

