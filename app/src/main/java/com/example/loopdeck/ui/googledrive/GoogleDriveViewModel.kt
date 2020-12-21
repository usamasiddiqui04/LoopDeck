package com.example.loopdeck.ui.googledrive

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.loopdeck.googledrive.DriveQuickstart
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import java.io.ByteArrayOutputStream
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

    fun getDrivefiles() {
        val result: FileList = service.files().list()
            .setPageSize(100)
            .execute()
        val files: List<File>? = result.files
        if (files == null || files.isEmpty()) {
            println("No files found.")
        } else {
            println("Files:")
            recentsMediaLiveData.value = files
        }
    }

    fun downloadfiles(resId: String, mintype: String) {

        try {
            val outputStream: OutputStream = ByteArrayOutputStream()
            service.files().export(resId, mintype)
                .executeMediaAndDownloadTo(outputStream)
        } catch (e: InvocationTargetException) {
            Toast.makeText(getApplication(), e.targetException.toString(), Toast.LENGTH_SHORT)
                .show()
        }


    }


}