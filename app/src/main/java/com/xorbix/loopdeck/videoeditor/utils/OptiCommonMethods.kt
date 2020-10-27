/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */
package com.xorbix.loopdeck.videoeditor.utils

import android.content.Context
import android.content.Intent
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import java.io.*
import java.nio.channels.FileChannel
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

object OptiCommonMethods {
    private val tagName = OptiCommonMethods::class.java.simpleName

    //write intent data into file
    fun writeIntoFile(context: Context, data: Intent, file: File): File {
        var videoAsset: AssetFileDescriptor? = null
        try {
            videoAsset = data.data?.let { context.contentResolver.openAssetFileDescriptor(it, "r") }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val `in`: FileInputStream
        try {
            `in` = videoAsset!!.createInputStream()
            var out: OutputStream? = null
            out = FileOutputStream(file)

            // Copy the bits from instream to outstream
            val buf = ByteArray(1024)
            var len: Int
            while (`in`.read(buf).also { len = it } > 0) {
                out.write(buf, 0, len)
            }
            `in`.close()
            out.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    //copy file from one source file to destination file
    @Throws(IOException::class)
    fun copyFile(sourceFile: File?, destFile: File) {
        if (!destFile.parentFile.exists()) destFile.parentFile.mkdirs()
        if (!destFile.exists()) {
            destFile.createNewFile()
        }
        var source: FileChannel? = null
        var destination: FileChannel? = null
        try {
            source = FileInputStream(sourceFile).channel
            destination = FileOutputStream(destFile).channel
            destination.transferFrom(source, 0, source.size())
        } finally {
            source?.close()
            destination?.close()
        }
    }

    //get video duration in seconds
    fun convertDurationInSec(duration: Long): Long {
        return TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(
                duration
            )
        )
    }

    //get video duration in minutes
    fun convertDurationInMin(duration: Long): Long {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        Log.v(tagName, "min: $minutes")
        return if (minutes > 0) {
            minutes
        } else {
            0
        }
    }

    //get video duration in minutes & seconds
    fun convertDuration(duration: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        Log.v(tagName, "min: $minutes")
        return if (minutes > 0) {
            minutes.toString() + ""
        } else {
            "00:" + (TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(duration)
            ))
        }
    }

    //get video duration based on uri
    fun getMediaDuration(context: Context?, uriOfFile: Uri?): Int {
        val mp = MediaPlayer.create(context, uriOfFile)
        return mp.duration
    }

    //get file extension based on file path
    fun getFileExtension(filePath: String): String {
        val extension = filePath.substring(filePath.lastIndexOf("."))
        Log.v(tagName, "extension: $extension")
        return extension
    }
}