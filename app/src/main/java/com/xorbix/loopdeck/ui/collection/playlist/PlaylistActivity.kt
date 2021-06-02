package com.xorbix.loopdeck.ui.collection.playlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xorbix.loopdeck.R
import com.xorbix.loopdeck.data.MediaData

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