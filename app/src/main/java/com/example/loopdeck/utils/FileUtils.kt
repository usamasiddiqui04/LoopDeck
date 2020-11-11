package com.example.loopdeck.utils

import android.content.Context
import android.system.Os.accept
import java.io.File
import java.io.FileFilter

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
}


fun File.isImage() = ImageFileFilter().accept(this)
fun File.isVideo() =VideoFileFilter().accept(this)

