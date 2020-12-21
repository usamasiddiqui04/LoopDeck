package com.example.loopdeck.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.loopdeck.R
import com.example.loopdeck.ui.viewholders.GoogleDrivetViewHolder
import com.example.loopdeck.ui.viewholders.ImageViewHolder
import com.example.loopdeck.ui.viewholders.VideoViewHolder
import com.example.loopdeck.utils.callbacks.ItemMoveCallback
import com.google.api.services.drive.model.File
import java.util.*

class GoogleDriveFileAdaptor(
    private var mList: MutableList<File>,
    private val itemClickListener: ((File) -> Unit)? = null,
    private val itemLongClickListener: ((View, File) -> Boolean)? = null,

    ) : Adapter<ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is GoogleDrivetViewHolder -> {
                holder.bind(mList.get(position), itemClickListener)
                holder.itemView.setOnLongClickListener {
                    itemLongClickListener?.invoke(it, mList[holder.adapterPosition]) ?: false
                }
            }
        }
    }


    fun submitList(list: List<File>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_IMAGE -> {

                ImageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recent_list_images, parent, false)
                )
            }
            VIEW_TYPE_VIDEO -> {
                VideoViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recent_video_lists, parent, false)
                )
            }
            else -> {
                GoogleDrivetViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_google_drive_folder_list, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val file = mList[position]
        return VIEW_TYPE_PLAYLIST
//        when {
//            file.mimeType.contains(".jpg") -> VIEW_TYPE_IMAGE
//            file.filePath.contains(".mp4") -> VIEW_TYPE_VIDEO
//            else -> VIEW_TYPE_PLAYLIST
//        }
    }


    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_VIDEO = 2
        const val VIEW_TYPE_PLAYLIST = 3
    }


}

