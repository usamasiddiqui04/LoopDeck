package com.xorbix.loopdeck.editor.soundpicker

import android.media.MediaMetadataRetriever
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.soundpickerlayout.view.*
import java.text.SimpleDateFormat
import java.util.*

class SongViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {


    fun bind(
        songinfo: Songinfo,
        onPlayPressed: (Songinfo) -> Unit,
        onPausePressed: (Songinfo) -> Unit
    ) {

        itemView.songtitle.text = songinfo.Title
        itemView.songalbum.text = songinfo.Author

        val duration = getDuration(songinfo.Duartion!!)
        val time = getDate(duration, "mm:ss")


        itemView.songduration.text = time.toString()

        itemView.play.setOnClickListener {

            onPlayPressed(songinfo)

        }

        itemView.pause.setOnClickListener {
            onPausePressed(songinfo)
        }

        if (songinfo.isPlaying) {
            itemView.play.visibility = View.GONE
            itemView.pause.visibility = View.VISIBLE
        } else {
            itemView.play.visibility = View.VISIBLE
            itemView.pause.visibility = View.GONE
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

    fun getDuration(uri: String): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val duration =
            java.lang.Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        retriever.release()

        return duration
//    }
    }
}