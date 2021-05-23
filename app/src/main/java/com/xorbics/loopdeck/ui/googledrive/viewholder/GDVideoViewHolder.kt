package com.xorbics.loopdeck.ui.googledrive.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.xorbics.loopdeck.R
import com.xorbics.loopdeck.imageloader.ImageLoader
import com.google.api.services.drive.model.File
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*


class GDVideoViewHolder(itemView: View) : ViewHolder(itemView) {
    fun bind(imageLoader: ImageLoader, file: File, itemClickListener: ((File) -> Unit)? = null) {

        itemView.setOnClickListener { itemClickListener?.invoke(file) }

        imageLoader.loadImage(
            file.thumbnailLink,
            itemView.imageViewRecentVideo,
            R.drawable.ic_baseline_movie,
            R.drawable.ic_baseline_movie
        )
    }
}