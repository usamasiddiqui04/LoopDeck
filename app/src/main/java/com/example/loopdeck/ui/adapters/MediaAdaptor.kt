package com.example.loopdeck.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.ui.viewholders.ImageViewHolder
import com.example.loopdeck.ui.viewholders.PlaylistViewHolder
import com.example.loopdeck.ui.viewholders.VideoViewHolder
import com.example.loopdeck.utils.callbacks.ItemMoveCallback
import java.util.*

class MediaAdaptor(
    private var mList: MutableList<MediaData>,
    private val itemClickListener: (View, ViewHolder, MutableList<MediaData>, MediaData) -> Unit,
    private val itemLongClickListener: (View, ViewHolder, MutableList<MediaData>, MediaData) -> Unit,
    private val onSequenceChanged: ((List<MediaData>) -> Unit)? = null

) : Adapter<ViewHolder>(), ItemMoveCallback.DragAndDropListener {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener {
                    itemClickListener.invoke(it, holder, mList, mList[position])
                }
                holder.itemView.setOnLongClickListener {
                    itemLongClickListener.invoke(it, holder, mList, mList[position])
                    true

                }
            }
            is VideoViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener {
                    itemClickListener.invoke(it, holder, mList, mList[position])
                }
                holder.itemView.setOnLongClickListener {
                    itemLongClickListener.invoke(it, holder, mList, mList[position])
                    true
                }
            }
            is PlaylistViewHolder -> {
                holder.bind(mList.get(position))
                holder.itemView.setOnClickListener {
                    itemClickListener.invoke(it, holder, mList, mList[position])
                }
                holder.itemView.setOnLongClickListener {
                    itemLongClickListener.invoke(it, holder, mList, mList[position])
                    true
                }

            }
        }
    }


    fun submitList(list: List<MediaData>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
        Log.d("MediaAdapter", mList.joinToString { "\n[${it.sequence}] ${it.name}" })
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
            file.filePath.contains(".jpg") -> VIEW_TYPE_IMAGE
            file.filePath.contains(".mp4") -> VIEW_TYPE_VIDEO
            else -> VIEW_TYPE_PLAYLIST
        }
    }


    override fun onRowMoved(fromPosition: Int, toPosition: Int) {
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

        onSequenceChanged?.invoke(mList)
    }


    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_VIDEO = 2
        const val VIEW_TYPE_PLAYLIST = 3
    }


}

