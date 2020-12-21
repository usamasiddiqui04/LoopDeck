package com.example.loopdeck.ui.googledrive

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.loopdeck.googledrive.DriveQuickstart
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList

class GoogleDriveViewModel(application: Application) : AndroidViewModel(application) {

    val recentsMediaLiveData = MutableLiveData<List<File>>()


    fun getDrivefiles(context: Context) {
        val HTTP_TRANSPORT = com.google.api.client.http.javanet.NetHttpTransport()
        val service: Drive =
            Drive.Builder(
                HTTP_TRANSPORT,
                DriveQuickstart.JSON_FACTORY,
                DriveQuickstart.getCredentials(context, HTTP_TRANSPORT)
            )
                .setApplicationName(DriveQuickstart.APPLICATION_NAME)
                .build()
        val result: FileList = service.files().list()
            .setPageSize(100)
            .setFields("nextPageToken, files(id, name)")
            .execute()
        val files: List<File>? = result.files
        if (files == null || files.isEmpty()) {
            println("No files found.")
        } else {
            println("Files:")
            recentsMediaLiveData.value = files
        }


    }


}