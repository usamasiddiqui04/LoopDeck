package com.example.loopdeck.editor.soundpicker

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.soundpickerlayout.view.*
import java.util.concurrent.TimeUnit

class SongViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {


    fun bind(
        songinfo: Songinfo,
        onPlayPressed: (Songinfo) -> Unit,
        onPausePressed: (Songinfo) -> Unit
    ) {

        itemView.songtitle.setText(songinfo.Title)
        itemView.songalbum.setText(songinfo.Author)

        val duration: Long
        duration = songinfo.Duartion!!.toLong()

        val milliseconds: Long = duration
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        itemView.songduration.setText("${minutes}:${seconds}")

        itemView.play.setOnClickListener {
//            itemView.play.visibility = View.GONE
//            itemView.pause.visibility = View.VISIBLE

            onPlayPressed(songinfo)

        }

        itemView.pause.setOnClickListener {
//            itemView.play.visibility = View.VISIBLE
//            itemView.pause.visibility = View.GONE
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
}