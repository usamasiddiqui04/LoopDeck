package com.example.loopdeck.editor

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.fragment_googlrdrive.toolbar
import java.text.SimpleDateFormat
import java.util.*

class PlayActivity : AppCompatActivity() {

    var videoIncrementer = 0
    var duration = 0
    var videoView: VideoView? = null
    private var mediaList = ArrayList<MediaData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        val bundle = intent.extras
        mediaList =
            bundle!!.getParcelableArrayList<MediaData>("videoFileList") as ArrayList<MediaData>
        videoView = findViewById<View>(R.id.playVideo) as VideoView

        setMediaController()

        btnplay.setOnClickListener {

            btnplay.visibility = View.GONE
            btnpause.visibility = View.VISIBLE
            btnrestart.visibility = View.GONE
            videoView!!.start()

        }

        btnrestart.setOnClickListener {
            videoIncrementer = 0
            btnplay.visibility = View.GONE
            btnpause.visibility = View.VISIBLE
            btnrestart.visibility = View.GONE
            setMediaController()
        }

        btnpause.setOnClickListener {
            btnplay.visibility = View.VISIBLE
            btnpause.visibility = View.GONE
            btnrestart.visibility = View.GONE
            videoView!!.pause()
        }

        toolbar.setNavigationIcon(R.drawable.ic_back_black)
        toolbar.setNavigationOnClickListener { onBackPressed() }

    }

    private fun setMediaController() {
        videoView!!.setVideoURI(Uri.parse(mediaList[videoIncrementer].filePath))
        videoView!!.requestFocus()

        videoView!!.setOnPreparedListener {
            if (mediaList[videoIncrementer].filePath.contains(".mp4"))
                videoView!!.start()

            val time = getDate(videoView!!.duration.toLong(), "mm:ss")
            videoTime.text = time.toString()
        }

        videoView!!.setOnCompletionListener { mp ->

            if (videoIncrementer == mediaList.size - 1) {
                mp!!.stop()
                btnplay.visibility = View.GONE
                btnpause.visibility = View.GONE
                btnrestart.visibility = View.VISIBLE

            } else {
                mp!!.stop()
                mp.reset()
                videoIncrementer = if (++videoIncrementer < mediaList.size) videoIncrementer else 0
                mp.setDataSource(mediaList[videoIncrementer].filePath)
                mp.prepare()
                mp.start()

            }
        }
    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }


}