package com.xorbics.loopdeck.ui.googledrive

import android.annotation.SuppressLint
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.xorbics.loopdeck.data.MediaDatabase
import com.xorbics.loopdeck.data.MediaRepository
import com.xorbics.loopdeck.googledrive.DriveQuickstart
import com.xorbics.loopdeck.utils.isImage
import com.xorbics.loopdeck.utils.isVideo
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


object GoogleDriveController {

    val googleDriveFilesLiveData = MutableLiveData<List<File>>()

    @SuppressLint("StaticFieldLeak")
    private var repository: MediaRepository? = null

    val HTTP_TRANSPORT = com.google.api.client.http.javanet.NetHttpTransport()

    lateinit var service: Drive

    var progressDialog: ProgressDialog? = null

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

        val mediaDao = MediaDatabase.getDatabase(application).mediaDao()
        repository = MediaRepository(mediaDao, application.applicationContext)

        service = Drive.Builder(
            HTTP_TRANSPORT,
            DriveQuickstart.JSON_FACTORY,
            DriveQuickstart.getCredentials(application, HTTP_TRANSPORT)
        )
            .setApplicationName(DriveQuickstart.APPLICATION_NAME)
            .build()

//        result = service.files()
//            .list()
//            .setFields("files/thumbnailLink, files/name, files/mimeType, files/id")
//            .setPageSize(100)
//            .execute()

        getDriveImages()
        progressDialog = ProgressDialog(application.applicationContext)




        initComplete = true
    }


    fun getDriveImages() {
        result = service.files().list()
            .setQ("mimeType='image/jpeg' or mimeType='video/mp4'")
            .setSpaces("drive, appDataFolder, photos")
            .setFields("files/thumbnailLink, files/name, files/mimeType, files/id")
            .setPageSize(1000)
            .execute()
        for (file in result.files) {
            System.out.printf(
                "Found file: %s (%s)\n",
                file.name, file.id
            )
        }
    }

    fun download(context: Context, scope: CoroutineScope, file: File, playlistName: String?) {

        if (!initComplete)
            return

        scope.launch(Dispatchers.IO) {
            downloadfiles(context, file, playlistName!!)
        }
    }


    fun downloadfiles(context: Context, file: File, playlistName: String) {
        try {

            if (!initComplete)
                return


            val newFile = java.io.File(context.getExternalFilesDir(null), destinationFolder)

            if (!newFile.exists()) {
                newFile.mkdirs()
            }

            val fileId: String = file.getId()
            val fileName: String = file.getName()
            val destinationPath = newFile.absolutePath + "/$fileName"
            val outputstream: OutputStream =
                FileOutputStream(destinationPath)


            val downloadableFile = service.files().get(fileId)

//            customProgressListener.
            downloadableFile.mediaHttpDownloader.setProgressListener(
                DownloadProgressListener(
                    destinationPath, playlistName
                )
            )
//                .setChunkSize(1000)

            downloadableFile.executeMediaAndDownloadTo(outputstream)

            outputstream.flush()
            outputstream.close()

        } catch (e: InvocationTargetException) {
            Toast.makeText(context, e.targetException.toString(), Toast.LENGTH_SHORT)
                .show()
        }
    }


    internal class DownloadProgressListener(val destinationPath: String, val playlistName: String) :
        MediaHttpDownloaderProgressListener {
        @Throws(IOException::class)
        override fun progressChanged(downloader: MediaHttpDownloader) {
            when (downloader.downloadState) {
                DownloadState.MEDIA_IN_PROGRESS -> {
                    Log.d("Google Drive Download", "${downloader.progress}")
                }
                DownloadState.MEDIA_COMPLETE -> {
                    Log.d("Google Drive Download", " File Downloaded")

                    if (playlistName.isNotEmpty()) {
                        if (java.io.File(destinationPath).isImage()) {
                            repository!!.addMediaOrPlaylist(
                                java.io.File(destinationPath),
                                playlistName

                            )
                        } else if (java.io.File(destinationPath).isVideo()) {
                            repository!!.addMediaOrPlaylist(
                                java.io.File(destinationPath),
                                playlistName
                            )
                        }
                    } else {
                        if (java.io.File(destinationPath).isImage()) {
                            repository!!.addMediaOrPlaylist(
                                java.io.File(destinationPath),
                                null

                            )
                        } else if (java.io.File(destinationPath).isVideo()) {
                            repository!!.addMediaOrPlaylist(
                                java.io.File(destinationPath),
                                null
                            )
                        }
                    }

                }
            }
        }

    }


}