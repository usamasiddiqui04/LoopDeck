package com.example.loopdeck.ui.collection.playlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.ui.collection.recents.RecentsFragment

class PlaylistActivity : AppCompatActivity() {

    var mediaData: MediaData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist)

        mediaData = intent!!.getParcelableExtra("mediaData")

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PlaylistFragment.newInstance(mediaData!!.name))
                .commitNow()
        }
    }
}