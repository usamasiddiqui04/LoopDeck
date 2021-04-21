package com.example.loopdeck.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.loopdeck.R
import com.example.loopdeck.data.PublishData
import com.example.loopdeck.ui.viewholders.PublishViewHolder

class PublishAdaptors(private var mList: MutableList<PublishData>) :
    RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return PublishViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.publish_row_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (holder) {
            is PublishViewHolder -> {
                holder.bind(mList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    fun submitList(list: List<PublishData>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

}