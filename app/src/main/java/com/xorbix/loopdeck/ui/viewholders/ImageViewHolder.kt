package com.xorbix.loopdeck.ui.viewholders

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.xorbix.loopdeck.data.MediaData
import kotlinx.android.synthetic.main.item_recent_list_images.view.*


class ImageViewHolder(itemView: View) : ViewHolder(itemView) {
    fun bind(
        mediaData: MediaData,
    ) {
        val uri = Uri.parse(mediaData.filePath)
        itemView.imageViewRecentImage.setImageURI(uri)
    }
}