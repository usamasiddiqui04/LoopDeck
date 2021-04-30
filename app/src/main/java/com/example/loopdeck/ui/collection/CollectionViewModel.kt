package com.example.loopdeck.ui.collection

import android.app.Application
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaDatabase
import com.example.loopdeck.data.MediaRepository
import com.example.loopdeck.gallery.model.GalleryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CollectionViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var recentsMediaLiveData: LiveData<List<MediaData>>

    lateinit var recentsPlaylistLiveData: LiveData<List<MediaData>>


    private val repository: MediaRepository

    init {
        val mediaDao = MediaDatabase.getDatabase(application).mediaDao()
        repository = MediaRepository(mediaDao, application.applicationContext)
        getRecents()
        getAllPlaylist()
    }


    private fun getRecents() {

        viewModelScope.launch {
            recentsMediaLiveData = repository.getAllRecentsMediaLiveData()
        }
    }

    private fun getAllPlaylist() {

        viewModelScope.launch {
            recentsPlaylistLiveData = repository.getAllPlaylistMediaLiveData()
        }
    }

    fun delete(mediaData: MediaData) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteMedia(mediaData)
        }
    }

    fun getPlaylistMedia(playlistName: String) =
        repository.getPlaylistMediaLiveData(playlistName)


    fun addMediaFiles(mediaList: List<GalleryData>, playlistName: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaList.forEach {
                repository.addMedia(it, playlistName)
            }
        }
    }

    fun dublicateMediafiles(mediaData: MediaData, playlistName: String? = null) {

        viewModelScope.launch(Dispatchers.IO) {
            repository.addDublicateMedia(File(mediaData.filePath), playlistName)
        }
    }

    fun editedImageFiles(file: File, playlistName: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addEditedFile(file, playlistName)
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