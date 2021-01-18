package com.example.loopdeck.ui.viewholders

import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.data.MediaData
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*

class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(
        mediaData: MediaData,
        itemClickListener: ((MediaData) -> Unit)? = null
    ) {
        val uri = Uri.parse(mediaData.filePath)
        val bitmap = ThumbnailUtils.createVideoThumbnail(
            uri.toString(),
            MediaStore.Video.Thumbnails.MINI_KIND
        )
        itemView.imageViewRecentVideo.setImageBitmap(bitmap)
        itemView.setOnClickListener { itemClickListener?.invoke(mediaData) }
    }
}