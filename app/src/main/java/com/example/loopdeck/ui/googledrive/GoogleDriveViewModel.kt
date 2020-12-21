package com.example.loopdeck.ui.googledrive

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaDatabase
import com.example.loopdeck.data.MediaRepository
import com.picker.gallery.model.GalleryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class GoogleDriveViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var recentsMediaLiveData: LiveData<List<com.google.api.services.drive.model.File>>




}