package com.xorbics.loopdeck.ui.googledrive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xorbics.loopdeck.R

class GoogleDriveActivity : AppCompatActivity() {

    var playlistName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_googledrive)

        playlistName = intent!!.getStringExtra("playlistName")


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, GoogleDriveFragment.newInstance(playlistName))
                .commitNow()
        }
    }
}