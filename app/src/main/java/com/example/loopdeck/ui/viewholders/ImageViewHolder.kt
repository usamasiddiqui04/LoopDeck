package com.example.loopdeck.ui.viewholders

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.loopdeck.data.MediaData
import kotlinx.android.synthetic.main.item_recent_list_images.view.*


class ImageViewHolder(itemView: View) : ViewHolder(itemView) {
    fun bind(
        mediaData: MediaData,
        itemClickListener: (MediaData) -> Unit
    ) {
        val uri = Uri.parse(mediaData.filePath)
        itemView.imageViewRecentImage.setImageURI(uri)
        itemView.setOnClickListener {
            itemClickListener?.invoke(mediaData)
        }
    }
}