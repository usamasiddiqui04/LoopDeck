package com.luminous.pick.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.luminous.pick.Adapter.ImageListRecyclerAdapter.VerticalItemHolder
import com.luminous.pick.CustomGallery
import com.luminous.pick.R
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener
import java.util.*

/**
 * Created by Kartum Infotech (Bhavesh Hirpara) on 06-Jul-18.
 */
class ImageListRecyclerAdapter(private val mContext: Context) :
    RecyclerView.Adapter<VerticalItemHolder>() {
    private val imageLoader: ImageLoader
    private val imageOptions: DisplayImageOptions
    var mItems = ArrayList<CustomGallery>()
    var isMultiSelected = false
        private set
    var mEventListener: EventListener? = null

    interface EventListener {
        fun onItemClickListener(position: Int, v: VerticalItemHolder)
    }

    val selected: ArrayList<CustomGallery>
        get() {
            val dataT = ArrayList<CustomGallery>()
            for (i in mItems.indices) {
                if (mItems[i].isSeleted) {
                    dataT.add(mItems[i])
                }
            }
            return dataT
        }

    fun addAll(files: ArrayList<CustomGallery>) {
        try {
            mItems.clear()
            mItems.addAll(files)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        notifyDataSetChanged()
    }

    fun changeSelection(v: VerticalItemHolder, position: Int) {
        if (mItems[position].isSeleted) {
            mItems[position].isSeleted = false
        } else {
            mItems[position].isSeleted = true
        }
        v.imgQueueMultiSelected.isSelected = mItems[position].isSeleted
        //((ImageListRecyclerAdapter.VerticalItemHolder) v.getTag()).imgQueueMultiSelected.setSelected(mItems.get(position).isSeleted);
    }

    fun clear() {
        mItems.clear()
        notifyDataSetChanged()
    }

    fun setMultiplePick(isMultiplePick: Boolean) {
        isMultiSelected = isMultiplePick
    }

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): VerticalItemHolder {
        val inflater = LayoutInflater.from(container.context)
        val root = inflater.inflate(R.layout.gallery_item, container, false)
        return VerticalItemHolder(root, this)
    }

    override fun onBindViewHolder(
        holder: VerticalItemHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = mItems[position]

//        imageLoader.displayImage(item.sdcardPath, holder.imgQueue);
        holder.setImage(item.sdcardPath)
        if (isMultiSelected) {
            holder.imgQueueMultiSelected.visibility = View.VISIBLE
        } else {
            holder.imgQueueMultiSelected.visibility = View.GONE
        }
        if (isMultiSelected) {
            holder.imgQueueMultiSelected.isSelected = item.isSeleted
        }
        holder.container.setOnClickListener {
            if (mEventListener != null) {
                mEventListener!!.onItemClickListener(position, holder)
            }
        }
    }

    override fun getItemCount(): Int {
        return mItems.size
    }

    fun getItem(position: Int): CustomGallery {
        return mItems[position]
    }

    inner class VerticalItemHolder(itemView: View, adapter: ImageListRecyclerAdapter?) :
        RecyclerView.ViewHolder(itemView) {
        //        //@BindView(R.id.imgQueue)
        var imgQueue: ImageView

        //        //@BindView(R.id.imgQueueMultiSelected)
        var imgQueueMultiSelected: ImageView

        //        //@BindView(R.id.container)
        var container: View
        fun setImage(url: String?) {
            imageLoader.displayImage(
                "file://$url",
                imgQueue, object : SimpleImageLoadingListener() {
                    override fun onLoadingStarted(imageUri: String, view: View) {
                        imgQueue
                            .setImageResource(R.drawable.no_media)
                        super.onLoadingStarted(imageUri, view)
                    }
                })
        }

        init {
            //            ButterKnife.bind(this, itemView);
            imgQueue = itemView.findViewById(R.id.imgQueue)
            imgQueueMultiSelected = itemView.findViewById(R.id.imgQueueMultiSelected)
            container = itemView.findViewById(R.id.container)
        }
    }

    fun setEventListner(eventListner: EventListener) {
        mEventListener = eventListner
    }

    //    private AdapterView.OnItemClickListener mOnItemClickListener;
    init {
        imageLoader = ImageLoader.getInstance()
        val config = ImageLoaderConfiguration.Builder(
            mContext
        ).build()
        imageLoader.init(config)
        imageOptions = DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .showImageOnLoading(R.drawable.no_media)
            .showImageForEmptyUri(R.drawable.no_media)
            .showImageOnFail(R.drawable.no_media)
            .build()
    }
}