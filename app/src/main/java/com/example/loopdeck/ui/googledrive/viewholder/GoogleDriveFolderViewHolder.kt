package com.example.loopdeck.ui.googledrive.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.R
import com.example.loopdeck.imageloader.ImageLoader
import com.google.api.services.drive.model.File
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*
import kotlinx.android.synthetic.main.item_recent_list_images.view.*

class GoogleDriveFolderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(imageLoader: ImageLoader, file: File, itemClickListener: ((File) -> Unit)? = null) {
        itemView.playlistName.text = file.name
        itemView.setOnClickListener { itemClickListener?.invoke(file) }

//        imageLoader.loadImage(
//            file.thumbnailLink,
//            itemView.imageViewRecentPlaylist,
//            R.drawable.ic_baseline_folder_24,
//            R.drawable.ic_baseline_folder_24
//        )


    }
}