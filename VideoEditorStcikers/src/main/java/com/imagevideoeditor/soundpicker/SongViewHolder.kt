package com.imagevideoeditor.soundpicker

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.soundpickerlayout.view.*
import java.util.concurrent.TimeUnit

class SongViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    var mediaPlayer: MediaPlayer = MediaPlayer()
    var retriever: MediaMetadataRetriever? = null
    fun bind(songinfo: Songinfo) {

        itemView.songtitle.setText(songinfo.Title)
        itemView.songalbum.setText(songinfo.Author)

        val duration: Long
        duration = songinfo.Duartion!!.toLong()

        val milliseconds: Long = duration
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        itemView.songduration.setText("${minutes}:${seconds}")
        mediaPlayer.setDataSource(songinfo.SongUrl)

        itemView.play.setOnClickListener {
            itemView.play.visibility = View.GONE
            itemView.pause.visibility = View.VISIBLE
            mediaPlayer.prepare()
            mediaPlayer.start()
        }

        itemView.pause.setOnClickListener {
            itemView.play.visibility = View.VISIBLE
            itemView.pause.visibility = View.GONE
            mediaPlayer.stop()
        }


    }
}