package com.imagevideoeditor.soundpicker

import android.content.Context
import android.media.MediaActionSound
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imagevideoeditor.R

class SongAdaptor(
    private var mList: ArrayList<Songinfo>, private var context: Context
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.soundpickerlayout, parent, false)
        return SongViewHolder(inflatedView)

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SongViewHolder -> {
                holder.bind(mList[position])
            }
        }
    }
}