package com.example.loopdeck.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.loopdeck.R
import com.example.loopdeck.ui.collection.CollectionActivity
import com.loopdeck.photoeditor.EditImageActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {

        btn_gallery.setOnClickListener {
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