package com.example.loopdeck

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.loopdeck.ui.recents.RecentsActivity
import com.loopdeck.photoeditor.EditImageActivity
import com.luminous.pick.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    private fun initViews() {

        btn_gallery.setOnClickListener {
            startActivity(Intent(this@MainActivity, MainActivity::class.java))
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
                    RecentsActivity::class.java
                )
            )
        }
    }
}