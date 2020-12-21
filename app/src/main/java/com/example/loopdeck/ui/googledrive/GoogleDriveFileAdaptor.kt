package com.example.loopdeck.ui.googledrive

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.loopdeck.R
import com.example.loopdeck.ui.viewholders.GoogleDriveListViewHolder
import com.example.loopdeck.ui.viewholders.ImageViewHolder
import com.example.loopdeck.ui.viewholders.PlaylistViewHolder
import com.example.loopdeck.ui.viewholders.VideoViewHolder
import com.example.loopdeck.utils.callbacks.ItemMoveCallback
import com.google.api.services.drive.model.File
import java.util.*
import kotlin.collections.ArrayList

class GoogleDriveFileAdaptor(
    private var mList: MutableList<File>,

    ) : Adapter<ViewHolder>() {


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {

            is GoogleDriveListViewHolder -> {
                holder.bind(mList.get(position))
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
        return PlaylistViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_recent_folder_list, parent, false)
        )
    }




}

