package com.xorbics.loopdeck.ui.collection.publish

import android.app.Application
import android.os.Environment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.xorbics.loopdeck.R
import com.xorbics.loopdeck.data.MediaDatabase
import com.xorbics.loopdeck.data.MediaRepository
import com.xorbics.loopdeck.data.PublishData
import com.xorbics.loopdeck.editor.PlayActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class PublishViewModel(application: Application) : AndroidViewModel(application) {

    lateinit var publishedLiveData: LiveData<List<PublishData>>
    private val repository: MediaRepository
    var downloadsDirectoryPath: String? = null

    init {
        val mediaDao = MediaDatabase.getDatabase(application).mediaDao()
        repository = MediaRepository(mediaDao, application.applicationContext)
        downloadsDirectoryPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
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

    fun saveSilentFileToMobileDevice(playActivity: PlayActivity) {

        val file = File(downloadsDirectoryPath, "silent.mp3")

        if (!file.exists()) {
            var input: InputStream? = null
            var fout: FileOutputStream? = null

            try {
                input = playActivity.resources.openRawResource(R.raw.silent)
                fout = FileOutputStream(File(downloadsDirectoryPath, "silent.mp3"))

                val data = ByteArray(1024)

                var count: Int
                while (input.read(data, 0, 1024).also { count = it } != -1) {
                    fout.write(data, 0, count)
                }
            } finally {
                input?.close()
                fout?.close()
            }
        }

    }

    fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }
}