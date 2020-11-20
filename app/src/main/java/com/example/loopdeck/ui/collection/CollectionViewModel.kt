package com.example.loopdeck.ui.collection

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaDatabase
import com.example.loopdeck.data.MediaRepository
import com.example.loopdeck.utils.FileUtils.uriToMediaFile
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import com.xorbix.loopdeck.cameraapp.BitmapUtils.SaveVideo
import com.xorbix.loopdeck.cameraapp.BitmapUtils.saveImage
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


    fun getPlaylistMedia(playlistName: String) =
        repository.getPlaylistMediaLiveData(playlistName)


    fun addMediaFiles(playlistName: String? = null) {
        val data = importedFilesIntent?.clipData
        if (data != null) {
            viewModelScope.launch {
                for (i in 0 until data.itemCount) {
                    repository.addMedia(data.getItemAt(i).uri, playlistName)
                }
            }

        } else {
            viewModelScope.launch {
                importedFilesIntent?.data?.let {
                    repository.addMedia(it, playlistName)
                }
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