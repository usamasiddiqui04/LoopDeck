package com.example.loopdeck.ui.googledrive

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.loopdeck.R
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.ui.collection.recents.RecentsFragment
import com.example.loopdeck.utils.extensions.activityViewModelProvider

class GoogleDriveActivity : AppCompatActivity() {

    lateinit var googleDriveViewModel: GoogleDriveViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleDriveViewModel = activityViewModelProvider()
        setContentView(R.layout.activity_googledrive)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, GoogleDriveFragment.newInstance())
                .commitNow()
        }
    }
}