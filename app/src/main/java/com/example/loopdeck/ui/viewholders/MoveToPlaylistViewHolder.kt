package com.example.loopdeck.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.data.MediaData
import kotlinx.android.synthetic.main.row_move_to_playlist.view.*

class MoveToPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(
        mediaData: MediaData,
    ) {

        itemView.filename.text = mediaData.name
    }
}