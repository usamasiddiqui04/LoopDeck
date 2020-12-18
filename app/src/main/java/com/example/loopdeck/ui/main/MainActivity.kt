package com.example.loopdeck.ui.main


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.example.loopdeck.R
import com.example.loopdeck.googledrive.DriveQuickstart
import com.example.loopdeck.ui.collection.CollectionActivity
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.loopdeck.photoeditor.EditImageActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        initViews()
    }

    private fun initViews() {

        btn_gallery.setOnClickListener {
//            val HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
////            val HTTP_TRANSPORT = com.google.api.client.http.javanet.NetHttpTransport()
//            val HTTP_TRANSPORT = com.google.api.client.http.apache.
//            val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()

            val HTTP_TRANSPORT = com.google.api.client.http.javanet.NetHttpTransport()

//            val HTTP_TRANSPORT = NetHttpTransport.Builder().trustCertificates(GoogleUtils.getCertificateTrustStore())
            val service: Drive =
                Drive.Builder(
                    HTTP_TRANSPORT,
                    DriveQuickstart.JSON_FACTORY,
                    DriveQuickstart.getCredentials(this, HTTP_TRANSPORT)
                )
                    .setApplicationName(DriveQuickstart.APPLICATION_NAME)
                    .build()



            // Print the names and IDs for up to 10 files.

            // Print the names and IDs for up to 10 files.
            val result: FileList = service.files().list()
                .setPageSize(100)
                .setFields("nextPageToken, files(id, name)")
                .execute()
            val files: List<File>? = result.files
            if (files == null || files.isEmpty()) {
                println("No files found.")
            } else {
                println("Files:")
                for (file in files) {

                    //TODO: Get thumbnail link here and assign it to imageView
                    file.thumbnailLink
//                    println("${file.getName()} ${file.getId()} ${file.fileExtension}")
                    println(file.toString())

                }
            }

//            startActivity(Intent(this@MainActivity, MainActivity::class.java))
        }

        btn_image.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    EditImageActivity::class.java
                )
            )
        }
        btn_video.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    com.obs.marveleditor.MainActivity::class.java
                )
            )
        }

        btn_recents.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    CollectionActivity::class.java
                )
            )
        }
    }



}