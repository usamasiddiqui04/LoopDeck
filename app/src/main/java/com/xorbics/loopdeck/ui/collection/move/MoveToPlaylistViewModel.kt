package com.xorbics.loopdeck.ui.collection.move

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.xorbics.loopdeck.data.MediaData
import com.xorbics.loopdeck.data.MediaDatabase
import com.xorbics.loopdeck.data.MediaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MoveToPlaylistViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var recentsPlaylistLiveData: LiveData<List<MediaData>>

    private val repository: MediaRepository

    init {
        val mediaDao = MediaDatabase.getDatabase(application).mediaDao()
        repository = MediaRepository(mediaDao, application.applicationContext)
        getAllPlaylist()

    }

    private fun getAllPlaylist() {

        viewModelScope.launch {
            recentsPlaylistLiveData = repository.getAllPlaylistMediaLiveData()
        }
    }


    fun dublicateMediafiles(mediaData: MediaData, playlistName: String? = null) {

        viewModelScope.launch(Dispatchers.IO) {
            repository.addDublicateMedia(File(mediaData.filePath), playlistName)
        }
    }

    fun delete(mediaData: MediaData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMedia(mediaData)
        }
    }


}