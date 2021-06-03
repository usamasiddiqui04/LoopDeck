package com.xorbix.loopdeck.ui.collection

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.xorbix.loopdeck.data.MediaData
import com.xorbix.loopdeck.data.MediaDatabase
import com.xorbix.loopdeck.data.MediaRepository
import com.xorbix.loopdeck.gallery.model.GalleryData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class CollectionViewModel(application: Application) : AndroidViewModel(application) {

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

    fun createPlaylist(file: File, playlistName: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addMediaOrPlaylist(file, playlistName)
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