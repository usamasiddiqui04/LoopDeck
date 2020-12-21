package com.example.loopdeck.ui.googledrive

import android.app.Application
import android.os.AsyncTask
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.loopdeck.googledrive.DriveQuickstart
import com.google.api.client.googleapis.media.MediaHttpDownloader
import com.google.api.client.googleapis.media.MediaHttpDownloader.DownloadState
import com.google.api.client.googleapis.media.MediaHttpDownloaderProgressListener
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.reflect.InvocationTargetException


class GoogleDriveViewModel(application: Application) : AndroidViewModel(application) {

    val recentsMediaLiveData = MutableLiveData<List<File>>()

    val HTTP_TRANSPORT = com.google.api.client.http.javanet.NetHttpTransport()

    val service: Drive =
        Drive.Builder(
            HTTP_TRANSPORT,
            DriveQuickstart.JSON_FACTORY,
            DriveQuickstart.getCredentials(application, HTTP_TRANSPORT)
        )
            .setApplicationName(DriveQuickstart.APPLICATION_NAME)
            .build()

    val result: FileList = service.files().list()
        .setPageSize(100)
        .execute()

    fun getDrivefiles() {

        val files: List<File>? = result.files
        if (files == null || files.isEmpty()) {
            println("No files found.")
        } else {
            println("Files:")
            recentsMediaLiveData.value = files
        }
    }


    suspend fun downloadfiles(resId: String, mintype: String) {
        try {
            val outputStream: OutputStream = ByteArrayOutputStream()
            service.apply {
                files().get(resId).mediaHttpDownloader.setProgressListener(CustomProgressListener())
                files().get(resId).executeMediaAndDownloadTo(outputStream)
            }
        } catch (e: InvocationTargetException) {
            Toast.makeText(getApplication(), e.targetException.toString(), Toast.LENGTH_SHORT)
                .show()
        }
    }


    internal class CustomProgressListener : MediaHttpDownloaderProgressListener {
        override fun progressChanged(downloader: MediaHttpDownloader) {
            when (downloader.downloadState) {
                DownloadState.MEDIA_IN_PROGRESS -> println(downloader.progress)
                DownloadState.MEDIA_COMPLETE -> println("Download is complete!")
            }
        }
    }


}