package com.example.loopdeck.editor

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.loopdeck.R
import kotlinx.android.synthetic.main.fragment_googlrdrive.*
import java.io.File

class PlayActivity : AppCompatActivity() {

    var videoUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        videoUri = Uri.parse(intent.getStringExtra("videoFilePath"))
        val videoView = findViewById<View>(R.id.playVideo) as VideoView
        //Set MediaController  to enable play, pause, forward, etc options.
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)

        videoView.setMediaController(mediaController)
        videoView.setVideoURI(videoUri)
        videoView.requestFocus()
        videoView.start()


        toolbar.setNavigationIcon(R.drawable.ic_back_black)
        toolbar.setNavigationOnClickListener { onBackPressed() }

    }
}