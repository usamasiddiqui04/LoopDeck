package com.example.loopdeck.ui.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.api.services.drive.model.File
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*

class GoogleDrivetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(File: File, itemClickListener: ((File) -> Unit)? = null) {
        itemView.playlistName.text = File.name
        itemView.setOnClickListener { itemClickListener?.invoke(File) }
    }
}