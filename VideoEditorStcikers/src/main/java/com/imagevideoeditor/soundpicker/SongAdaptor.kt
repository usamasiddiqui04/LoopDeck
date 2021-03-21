package com.imagevideoeditor.soundpicker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.imagevideoeditor.R

class SongAdaptor(
    private var mList: ArrayList<Songinfo>,
    private var context: Context,
    private val itemClickListener: (View, RecyclerView.ViewHolder, Songinfo) -> Unit,
    val onPlayPressed: (Songinfo) -> Unit,
    val onPausePressed: (Songinfo) -> Unit

) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var filterList: ArrayList<Songinfo> = mList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.soundpickerlayout, parent, false)
        return SongViewHolder(inflatedView)

    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SongViewHolder -> {
                val songinfo: Songinfo = filterList.get(position)
                holder.bind(songinfo, onPlay, onPause)
                holder.itemView.setOnClickListener {
                    itemClickListener.invoke(it, holder, songinfo)
                }
            }

        }
    }

    private val onPlay: (Songinfo) -> Unit = { songInfo ->
        onPlayPressed(songInfo)
        filterList.forEach {
            it.isPlaying = false
        }
        songInfo.isPlaying = true
        notifyDataSetChanged()
    }

    private val onPause: (Songinfo) -> Unit = { songInfo ->
        onPausePressed(songInfo)
        filterList.forEach {
            it.isPlaying = false
        }
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filterList = mList
                } else {
                    val finalfilteredList: ArrayList<Songinfo> = ArrayList()
                    for (row: Songinfo in mList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.Title!!.toLowerCase()
                                .contains(charString.toLowerCase())
                        ) {
                            finalfilteredList.add(row)
                        }
                    }
                    filterList = finalfilteredList
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                filterList = filterResults.values as ArrayList<Songinfo>
                notifyDataSetChanged()
            }
        }
    }
}