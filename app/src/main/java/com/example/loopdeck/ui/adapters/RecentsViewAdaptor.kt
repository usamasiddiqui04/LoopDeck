package com.example.loopdeck.ui.adapters

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.loopdeck.R
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import com.loopdeck.photoeditor.EditImageActivity
import com.obs.marveleditor.MainActivity
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*
import kotlinx.android.synthetic.main.item_recent_list_images.view.*
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*
import kotlin.coroutines.coroutineContext

class RecentsViewAdaptor(var mList: MutableList<File> , private val itemClickListener: (File) -> Unit) : Adapter<ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener{
                    itemClickListener(mList[position])
                }
            }
            is VideoViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener{
                    itemClickListener(mList[position])
                }
            }
            is PlaylistViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener{
                    itemClickListener(mList[position])
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
            file.isImage() -> VIEW_TYPE_IMAGE
            file.isVideo() -> VIEW_TYPE_VIDEO
            else -> VIEW_TYPE_PLAYLIST
        }
    }


    inner class ImageViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(file: File) {
            val uri = Uri.parse(file.toString())
            itemView.imageViewRecentImage.setImageURI(uri)

        }
    }

    inner class VideoViewHolder(itemView: View) : ViewHolder(itemView) {
        fun bind(file: File) {
            val uri = Uri.parse(file.toString())
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
        fun bind(file: File) {
            val uri = Uri.parse(file.toString())
            itemView.playlistName.setText(file.name)
        }
    }
    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_VIDEO = 2
        const val VIEW_TYPE_PLAYLIST = 3
    }


}

