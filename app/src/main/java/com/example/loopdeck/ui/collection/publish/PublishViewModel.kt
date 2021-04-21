package com.example.loopdeck.ui.collection.publish

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.loopdeck.data.MediaDatabase
import com.example.loopdeck.data.MediaRepository
import com.example.loopdeck.data.PublishData
import com.example.loopdeck.gallery.model.GalleryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PublishViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var publishedLiveData: LiveData<List<PublishData>>
    private val repository: MediaRepository

    init {
        val mediaDao = MediaDatabase.getDatabase(application).mediaDao()
        repository = MediaRepository(mediaDao, application.applicationContext)
        getPublish()
    }


    private fun getPublish() {
        viewModelScope.launch {
            publishedLiveData = repository.getAllPublihsedMediaLiveData()
        }
    }

    fun publishedFiles(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addPublishedFileData(file)
        }
    }
}