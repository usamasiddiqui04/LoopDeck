package com.xorbics.loopdeck.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xorbics.loopdeck.R
import com.xorbics.loopdeck.data.MediaData
import com.xorbics.loopdeck.ui.viewholders.MoveToPlaylistViewHolder


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