package com.example.loopdeck.ui.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaType
import com.example.loopdeck.editor.PreviewPhotoActivity
import com.example.loopdeck.editor.PreviewVideoActivity
import com.example.loopdeck.ui.collection.playlist.PlaylistActivity
import com.example.loopdeck.ui.googledrive.GoogleDriveAdaptor.Companion.VIEW_TYPE_PLAYLIST
import com.example.loopdeck.ui.viewholders.ImageViewHolder
import com.example.loopdeck.ui.viewholders.PlaylistViewHolder
import com.example.loopdeck.ui.viewholders.VideoViewHolder
import com.example.loopdeck.utils.callbacks.ItemMoveCallback
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*
import kotlinx.android.synthetic.main.item_recent_list_images.view.*
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*
import java.util.*


class MediaAdaptor(
    private var mList: MutableList<MediaData>,
    private val onSequenceChanged: ((List<MediaData>) -> Unit)? = null,
    private val context: Context

) : Adapter<ViewHolder>(), ItemMoveCallback.DragAndDropListener {

    var multiSelection: Boolean = false
    var string: MediaData? = null
    private var selectedIndex = -1
    private var selectedList = ArrayList<MediaData>()

    private var itemClick: OnItemClick? = null

    fun setItemClick(itemClick: OnItemClick?) {
        this.itemClick = itemClick
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener {
                    onclick(holder, mList[position], mList, position)
                }
                holder.itemView.setOnLongClickListener {
                    multiSelection = true
                    itemClick!!.onItemClick(multiSelection)
                    toggleSelection(holder, mList[position], mList, position)
                    true

                }
            }
            is VideoViewHolder -> {
                holder.bind(mList[position])
                holder.itemView.setOnClickListener {
                    onclick(holder, mList[position], mList, position)
                }
                holder.itemView.setOnLongClickListener {
                    multiSelection = true
                    itemClick?.onItemClick(multiSelection)
                    toggleSelection(holder, mList[position], mList, position)
                    true

                }
            }
            is PlaylistViewHolder -> {
                holder.bind(mList.get(position))
                holder.itemView.setOnClickListener {
                    onclick(holder, mList[position], mList, position)
                }
                holder.itemView.setOnLongClickListener {
                    multiSelection = true
                    itemClick?.onItemClick(multiSelection)
                    toggleSelection(holder, mList[position], mList, position)
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
    }


    private fun toggleSelection(
        viewHolder: ViewHolder,
        mediadata: MediaData,
        list: MutableList<MediaData>,
        position: Int
    ) {
        string = list[viewHolder.adapterPosition]
        when (mediadata.mediaType) {
            MediaType.IMAGE -> {

                if (viewHolder.itemView.selectitemimages.visibility == View.GONE) {
                    viewHolder.itemView.selectitemimages.visibility = View.VISIBLE
                    viewHolder.itemView.cardview.alpha = 0.5f
                    selectedList.add(string!!)
                    if (selectedIndex == position) selectedIndex = -1;

                } else if (viewHolder.itemView.selectitemimages.visibility == View.VISIBLE) {
                    viewHolder.itemView.selectitemimages.visibility = View.GONE
                    viewHolder.itemView.cardview.alpha = 1f
                    selectedList.remove(string!!)
                    if (selectedIndex == position) selectedIndex = -1;
                } else if (getSelectedList().size == 0) {
                    viewHolder.itemView.selectitemimages.visibility = View.VISIBLE
                    viewHolder.itemView.cardview.alpha = 0.5f
                }
            }
            MediaType.VIDEO -> {
                if (viewHolder.itemView.selectitemvideo.visibility == View.GONE) {
                    viewHolder.itemView.selectitemvideo.visibility = View.VISIBLE
                    viewHolder.itemView.cardvideo.alpha = 0.5f
                    selectedList.add(string!!)
                    if (selectedIndex == position) selectedIndex = -1;
                } else {
                    viewHolder.itemView.selectitemvideo.visibility = View.GONE
                    viewHolder.itemView.cardvideo.alpha = 1f
                    selectedList.remove(string!!)
                    if (selectedIndex == position) selectedIndex = -1

                }
            }
            else -> {
                if (viewHolder.itemView.selectitemfolder.visibility == View.GONE) {
                    viewHolder.itemView.selectitemfolder.visibility = View.VISIBLE
                    viewHolder.itemView.cardfolder.alpha = 0.5f
                    selectedList.add(string!!)
                    if (selectedIndex == position) selectedIndex = -1;
                } else {
                    viewHolder.itemView.selectitemfolder.visibility = View.GONE
                    viewHolder.itemView.cardfolder.alpha = 1f
                    selectedList.remove(string!!)
                    if (selectedIndex == position) selectedIndex = -1;
                }
            }
        }
    }

    fun onclick(
        viewHolder: ViewHolder,
        mediadata: MediaData,
        list: MutableList<MediaData>,
        position: Int
    ) {

        multiSelection = !selectedList.isEmpty()
        if (!multiSelection) {
            when (mediadata.mediaType) {
                MediaType.IMAGE -> {
                    val intent = Intent(context, PreviewPhotoActivity::class.java)
                    intent.putExtra("mediaData", mediadata)
                    context.startActivity(intent)
                }
                MediaType.VIDEO -> {
                    val intent = Intent(context, PreviewVideoActivity::class.java)
                    intent.putExtra("mediaData", mediadata)
                    context.startActivity(intent)

                }
                else -> {

                    val intent = Intent(context, PlaylistActivity::class.java)
                    intent.putExtra("mediaData", mediadata)
                    context.startActivity(intent)
                }
            }
//
        } else {

            toggleSelection(viewHolder, mediadata, list, position)
            multiSelection = !selectedList.isEmpty()
            itemClick!!.onItemClick(multiSelection)
        }


    }

    fun getSelectedList(): ArrayList<MediaData> {
        return selectedList
    }

    fun setSeletedList() {
        selectedList.clear()
    }


    interface OnItemClick {
        fun onItemClick(multiselection: Boolean)
    }


}


