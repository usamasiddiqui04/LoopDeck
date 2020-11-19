package com.example.loopdeck.ui.adapters

import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.ui.recents.RecentsViewModel
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*
import kotlinx.android.synthetic.main.item_recent_list_images.view.*
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*
import java.io.File
import java.util.*

class RecentsViewAdaptor(
    var mList: MutableList<MediaData>,
    private val itemClickListener: (String) -> Unit
) : Adapter<ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.bind(mList.get(position).file_path)
                holder.itemView.setOnClickListener {
                    itemClickListener(mList.get(position).file_path)
                }
            }
            is VideoViewHolder -> {
                holder.bind(mList.get(position).file_path)
                holder.itemView.setOnClickListener {
                    itemClickListener(mList.get(position).file_path)
                }
            }
            is PlaylistViewHolder -> {
                holder.bind(mList.get(position).file_path)
                holder.itemView.setOnClickListener {
                    itemClickListener(mList.get(position).file_path)
                }
            }
        }
    }


    fun submitList(list: List<MediaData>) {
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
                PlaylistViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_recent_folder_list, parent, false)
                )
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val file = mList[position]
        return when {
            file.file_path.contains(".jpg") -> VIEW_TYPE_IMAGE
            file.file_path.contains(".mp4") -> VIEW_TYPE_VIDEO
            else -> VIEW_TYPE_PLAYLIST
        }
    }


    inner class ImageViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(list: String) {
            val uri = Uri.parse(list)
            itemView.imageViewRecentImage.setImageURI(uri)

        }
    }

    inner class VideoViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(list: String) {
            val uri = Uri.parse(list.toString())
            val bitmap = ThumbnailUtils.createVideoThumbnail(
                uri.toString(),
                MediaStore.Video.Thumbnails.MINI_KIND
            )
            itemView.imageViewRecentVideo.setImageBitmap(bitmap)
        }
    }

    fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mList, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    inner class PlaylistViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(file: String) {
            itemView.playlistName.setText(file)
        }
    }

    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_VIDEO = 2
        const val VIEW_TYPE_PLAYLIST = 3
    }


}

