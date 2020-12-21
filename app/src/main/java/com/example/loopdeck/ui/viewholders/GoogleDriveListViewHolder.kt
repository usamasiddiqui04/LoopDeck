package com.example.loopdeck.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.data.MediaData
import com.google.api.services.drive.model.File
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*

class GoogleDriveListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(mediaData: File) {
        itemView.playlistName.text = mediaData.name
    }
}