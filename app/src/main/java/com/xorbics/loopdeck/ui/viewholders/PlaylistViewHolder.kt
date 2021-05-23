package com.xorbics.loopdeck.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.xorbics.loopdeck.data.MediaData
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
        mediaData: MediaData,
    ) {

        itemView.playlistName.text = mediaData.name
    }
}