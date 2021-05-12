package com.example.loopdeck.ui.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.PublishData
import com.example.loopdeck.editor.PlayActivity
import com.example.loopdeck.ui.viewholders.PublishViewHolder
import java.util.ArrayList

class PublishAdaptors(private var mList: MutableList<PublishData>, private var context: Context) :
    RecyclerView.Adapter<ViewHolder>() {
    private var selectedList = ArrayList<PublishData>()
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
                holder.itemView.setOnClickListener {
                    val intent = Intent(context, PlayActivity::class.java)
                    val bundle = Bundle()
                    bundle.putString("filePath", mList[position].filePath)
                    bundle.putBoolean("isPublishedVideo", true)
                    intent.putExtras(bundle)
                    context.startActivity(intent)
                }
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