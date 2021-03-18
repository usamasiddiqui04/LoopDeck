package com.example.loopdeck.ui.viewholders

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.data.MediaData
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*


class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
        mediaData: MediaData,
    ) {

        mediaData.thumbnail?.let {
            val uri = Uri.parse(it)
            itemView.imageViewRecentPlaylist.setImageURI(uri)
        }

        itemView.playlistName.text = mediaData.name

    }
}