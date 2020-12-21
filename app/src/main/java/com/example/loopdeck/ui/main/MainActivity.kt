package com.example.loopdeck.ui.main


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.example.loopdeck.R
import com.example.loopdeck.ui.collection.CollectionActivity
import com.example.loopdeck.ui.googledrive.GoogleDriveActivity
import com.google.api.services.drive.model.File
import com.loopdeck.photoeditor.EditImageActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var list: ArrayList<File>? = null

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

            startActivity(
                Intent(
                    this@MainActivity,
                    GoogleDriveActivity::class.java
                )
            )

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