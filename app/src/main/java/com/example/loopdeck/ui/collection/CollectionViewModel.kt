package com.example.loopdeck.ui.collection

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

class CollectionViewModel(application: Application) : AndroidViewModel(application) {

    var importedFilesIntent: Intent? = null

    lateinit var recentsMediaLiveData: LiveData<List<MediaData>>

    private val repository: MediaRepository

    init {
        val mediaDao = MediaDatabase.getDatabase(application).mediaDao()
        repository = MediaRepository(mediaDao, application.applicationContext)
        getRecents()
    }

    private fun getRecents() {

        viewModelScope.launch {
            recentsMediaLiveData = repository.getAllRecentsMediaLiveData()
        }
    }


    fun delete(mediaData: MediaData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMedia(mediaData)
        }
    }

    fun getPlayListImage(playlistName: String) =
        repository.getPlaylistImage(playlistName)

    fun getPlaylistMedia(playlistName: String) =
        repository.getPlaylistMediaLiveData(playlistName)


    fun addMediaFiles(mediaList: List<GalleryData>, playlistName: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaList.forEach {
                repository.addMedia(it, playlistName)
            }
        }
    }
    fun createPlaylist(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMediaOrPlaylist(file)
        }
    }
    fun onSequenceChanged(mList: List<MediaData>) {
        Handler().postDelayed({
            viewModelScope.launch(Dispatchers.IO) {

                mList.forEachIndexed { index, mediaData ->
                    mediaData.sequence = index + 1
                }
                repository.updatePlaylist(mList)
                Log.d("MediaAdapter", mList.joinToString { "\n[${it.sequence}] ${it.name}" })
            }
        }, 1500)
    }
}