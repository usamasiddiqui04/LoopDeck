package com.example.loopdeck.ui.adapters

import android.util.Log
import android.util.SparseBooleanArray
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
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*
import kotlinx.android.synthetic.main.item_recent_folder_list.view.selectitem
import kotlinx.android.synthetic.main.item_recent_list_images.view.*
import java.util.*


class MediaAdaptor(
    private var mList: MutableList<MediaData>,
    private val itemClickListener: (View, ViewHolder, MutableList<MediaData>, MediaData) -> Unit,
    private val itemLongClickListener: (View, ViewHolder, MutableList<MediaData>, MediaData) -> Unit,
    private val onSequenceChanged: ((List<MediaData>) -> Unit)? = null

) : Adapter<ViewHolder>(), ItemMoveCallback.DragAndDropListener {


    private var itemClick: OnItemClick? = null
    private val selectedItems: SparseBooleanArray? = null
    private var selectedIndex = -1

    fun setItemClick(itemClick: OnItemClick?) {
        this.itemClick = itemClick
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener {
                    itemClick!!.onItemClick(it, mList[position], position)
                }
                holder.itemView.setOnLongClickListener {
                    if (itemClick == null) {
                        return@setOnLongClickListener false
                    } else {
                        itemClick?.onLongPress(it, mList[position], position);
                        return@setOnLongClickListener true
                    }
                }
                toggleIcon(holder, position)

            }
            is VideoViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener {
                    itemClick!!.onItemClick(it, mList[position], position)
                }
                holder.itemView.setOnLongClickListener {
                    if (itemClick == null) {
                        return@setOnLongClickListener false
                    } else {
                        itemClick?.onLongPress(it, mList[position], position);
                        return@setOnLongClickListener true
                    }
                }
                toggleIcon(holder, position);
            }
            is PlaylistViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener {
                    itemClick!!.onItemClick(it, mList[position], position)
                }
                holder.itemView.setOnLongClickListener {
                    if (itemClick == null) {
                        return@setOnLongClickListener false
                    } else {
                        itemClick?.onLongPress(it, mList[position], position);
                        return@setOnLongClickListener true
                    }
                }
                toggleIcon(holder, position);

            }
        }

    }

    private fun toggleIcon(viewHolder: ViewHolder, position: Int) {

        if (selectedItems!!.get(position, false)) {
            viewHolder.itemView.selectitem.visibility = View.VISIBLE
            viewHolder.itemView.cardview.alpha = 0.5f
            if (selectedIndex == position) selectedIndex = -1;
        } else {
            viewHolder.itemView.selectitem.visibility = View.GONE
            viewHolder.itemView.cardview.alpha = 1f
            if (selectedIndex == position) selectedIndex = -1;
        }
    }

    fun getSelectedItems(): List<Int>? {
        val items: MutableList<Int> = ArrayList(selectedItems!!.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }

    fun removeItems(position: Int) {
        mList.removeAt(position)
        selectedIndex = -1
    }

    fun clearSelection() {
        selectedItems!!.clear()
        notifyDataSetChanged()
    }

    fun toggleSelection(position: Int) {
        selectedIndex = position
        if (selectedItems!![position, false]) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }

    fun selectedItemCount(): Int {
        return selectedItems!!.size()
    }


    interface OnItemClick {
        fun onItemClick(view: View?, mediaData: MediaData?, position: Int)
        fun onLongPress(view: View?, mediaData: MediaData??, position: Int)
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
            file.filePath.contains(".JPG") -> VIEW_TYPE_IMAGE
            file.filePath.contains(".jpeg") -> VIEW_TYPE_IMAGE
            file.filePath.contains(".mp4") -> VIEW_TYPE_VIDEO
            file.filePath.contains(".MP4") -> VIEW_TYPE_VIDEO
            file.filePath.contains(".png") -> VIEW_TYPE_IMAGE
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

