package com.example.loopdeck.ui.playlistrecnts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.loopdeck.utils.FileUtils
import java.io.File

class PlaylistRecntsViewModel : ViewModel() {
    // TODO: Implement the ViewModel
    val playlistName = MutableLiveData<String?>()
    val recentsMediaList = MutableLiveData<List<File>>()

    fun loadRecentList(context: Context) {
        val rootDir = FileUtils.getPlaylistDirectory(context , playlistName.value)
        recentsMediaList.value = rootDir.listFiles().toList().sortedBy { it.lastModified() }
    }


}