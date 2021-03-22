package com.example.loopdeck.ui.googledrive

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.loopdeck.googledrive.DriveQuickstart
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.googleapis.media.MediaHttpDownloader.DownloadState
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.reflect.InvocationTargetException
import kotlin.jvm.Throws

object GoogleDriveController {

    val googleDriveFilesLiveData = MutableLiveData<List<File>>()

    val HTTP_TRANSPORT = com.google.api.client.http.javanet.NetHttpTransport()

    lateinit var service: Drive

    var initComplete: Boolean = false

    var destinationFolder = "/GoogleDriveFiles/"

    lateinit var result: FileList

    fun getDrivefiles() {

        if (!initComplete)
            return

        val files: List<File>? = result.files
        if (files == null || files.isEmpty()) {
            println("No files found.")
        } else {
            println("Files:")
            googleDriveFilesLiveData.value = files
        }
    }


    fun init(application: Application) {

        service = Drive.Builder(
            HTTP_TRANSPORT,
            DriveQuickstart.JSON_FACTORY,
            DriveQuickstart.getCredentials(application, HTTP_TRANSPORT)
        )
            .setApplicationName(DriveQuickstart.APPLICATION_NAME)
            .build()


        result = service.files()
            .list()
            .setFields("files/thumbnailLink, files/name, files/mimeType, files/id")
            .setPageSize(100)
            .execute()

        initComplete = true
    }

    fun download(context: Context, scope: CoroutineScope, file: File) {

        if (!initComplete)
            return

        scope.launch(Dispatchers.IO) {
            downloadfiles(context, file)
        }
    }


    fun downloadfiles(context: Context, file: File) {
        try {

            if (!initComplete)
                return


            val newFile = java.io.File(context.getExternalFilesDir(null), destinationFolder)

            if (!newFile.exists()) {
                newFile.mkdirs()
            }

            val fileId: String = file.getId()
            val fileName: String = file.getName()
            val outputstream: OutputStream =
                FileOutputStream(newFile.absolutePath + "/$fileName")


            val downloadableFile = service.files().get(fileId)

//            customProgressListener.
            downloadableFile.mediaHttpDownloader.setProgressListener(DownloadProgressListener())
//                .setChunkSize(1000)

            downloadableFile.executeMediaAndDownloadTo(outputstream)

            outputstream.flush()
            outputstream.close()


        } catch (e: InvocationTargetException) {
            Toast.makeText(context, e.targetException.toString(), Toast.LENGTH_SHORT)
                .show()
        }
    }


    internal class DownloadProgressListener :
        MediaHttpDownloaderProgressListener {
        @Throws(IOException::class)
        override fun progressChanged(downloader: MediaHttpDownloader) {
            when (downloader.downloadState) {
                DownloadState.MEDIA_IN_PROGRESS -> {
                    Log.d("Google Drive Download", "${downloader.progress}")
                }
                DownloadState.MEDIA_COMPLETE -> {
                    Log.d("Google Drive Download", " File Downloaded")
                }
            }
        }
    }

}