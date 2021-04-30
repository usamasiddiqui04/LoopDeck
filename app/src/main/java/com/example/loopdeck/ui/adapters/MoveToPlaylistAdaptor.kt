package com.example.loopdeck.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.PublishData
import com.example.loopdeck.ui.viewholders.ImageViewHolder
import com.example.loopdeck.ui.viewholders.MoveToPlaylistViewHolder
import com.example.loopdeck.ui.viewholders.PlaylistViewHolder
import com.example.loopdeck.ui.viewholders.VideoViewHolder


class MoveToPlaylistAdaptor(
    private var mList: MutableList<MediaData>,
    private val itemClickListener: (MediaData) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MoveToPlaylistViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_move_to_playlist, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MoveToPlaylistViewHolder -> {
                holder.itemView.setOnClickListener {
                    itemClickListener.invoke(mList[position])
                }
                holder.bind(mList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun submitList(list: List<MediaData>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }


}