package com.xorbix.loopdeck.ui.googledrive

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.xorbix.loopdeck.R
import com.xorbix.loopdeck.imageloader.ImageLoader
import com.xorbix.loopdeck.ui.googledrive.viewholder.GDImageViewHolder
import com.xorbix.loopdeck.ui.googledrive.viewholder.GDVideoViewHolder
import com.xorbix.loopdeck.ui.googledrive.viewholder.GoogleDriveFolderViewHolder
import com.google.api.services.drive.model.File

class GoogleDriveAdaptor(
    private val imageLoader: ImageLoader,
    private var mList: MutableList<File>,
    private val itemClickListener: ((File) -> Unit)? = null,
    private val itemLongClickListener: ((View, File) -> Boolean)? = null,

    ) : Adapter<ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is GoogleDriveFolderViewHolder -> {
                holder.bind(imageLoader, mList.get(position), itemClickListener)
            }

            is GDImageViewHolder -> {
                holder.bind(imageLoader, mList.get(position), itemClickListener)
            }
            is GDVideoViewHolder -> {
                holder.bind(imageLoader, mList.get(position), itemClickListener)
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

                GDImageViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recent_list_images, parent, false)
                )
            }
            VIEW_TYPE_VIDEO -> {
                GDVideoViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recent_video_lists, parent, false)
                )
            }
            else -> {
                GoogleDriveFolderViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recent_folder_list, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val file = mList[position]

//        Log.d(
//            "Drive File: ",
//            file.name + ", mintype: ${file.mimeType}, thumbnail: ${file.webContentLink}"
//        )

        Log.d("Drive File ${file.size}: ", file.toString())


        return when {
            file.mimeType.contains("image/") -> VIEW_TYPE_IMAGE
            file.mimeType.contains("application/vnd.google-apps.folder") -> VIEW_TYPE_PLAYLIST
            file.mimeType.contains("video/") -> VIEW_TYPE_VIDEO
            else -> VIEW_TYPE_PLAYLIST
        }
    }


    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_VIDEO = 2
        const val VIEW_TYPE_PLAYLIST = 3
    }


}

