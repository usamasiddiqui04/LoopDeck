package com.example.loopdeck.ui.collection

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.loopdeck.R
import com.example.loopdeck.ui.collection.recents.RecentsFragment
import com.example.loopdeck.utils.extensions.activityViewModelProvider

class CollectionActivity : AppCompatActivity() {

    lateinit var collectionViewModel: CollectionViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        collectionViewModel = activityViewModelProvider()
        setContentView(R.layout.activity_recents)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RecentsFragment.newInstance())
                .commitNow()
        }
    }
}