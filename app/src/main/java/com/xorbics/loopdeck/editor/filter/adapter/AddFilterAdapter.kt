package com.xorbics.loopdeck.editor.filter.adapter

import android.media.ThumbnailUtils
import android.provider.MediaStore
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imagevideoeditor.R
import com.xorbics.loopdeck.editor.filter.utils.FilterType
import com.xorbics.loopdeck.editor.filter.utils.MotionTouchListener
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter
import kotlinx.android.synthetic.main.item_filter.view.*
import java.lang.NullPointerException

class AddFilterAdapter(
    private val mFilename: String,
    private val itemClickListener: (View, Int) -> Unit
) : RecyclerView.Adapter<AddFilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, mFilename)
        holder.itemView.setOnClickListener {
            itemClickListener.invoke(it, position)
        }
    }

    override fun getItemCount(): Int {
        return FilterType.createFilterList().size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(position: Int, filename: String) {

            itemView.apply {
                setOnTouchListener(MotionTouchListener())
            }

            var bitmap = ThumbnailUtils.createVideoThumbnail(
                filename,
                MediaStore.Video.Thumbnails.MICRO_KIND
            )

            val filterFile = arrayListOf<String>()
            filterFile.addAll(
                listOf(
                    "acv/afterglow.acv",
                    "acv/alice_in_wonderland.acv",
                    "acv/ambers.acv",
                    "acv/aurora.acv",
                    "acv/blue_poppies.acv",
                    "acv/blue_yellow_field.acv",
                    "acv/carousel.acv",
                    "acv/cold_desert.acv",
                    "acv/cold_heart.acv",
                    "acv/digital_film.acv",
                    "acv/documentary.acv",
                    "acv/electric.acv",
                    "acv/ghosts_in_your_head.acv",
                    "acv/good_luck_charm.acv",
                    "acv/green_envy.acv",
                    "acv/hummingbirds.acv",
                    "acv/kiss_kiss.acv",
                    "acv/left_hand_blues.acv",
                    "acv/light_parades.acv",
                    "acv/lullabye.acv",
                    "acv/moth_wings.acv",
                    "acv/moth_wings.acv",
                    "acv/old_postcards_01.acv",
                    "acv/old_postcards_02.acv",
                    "acv/peacock_feathers.acv",
                    "acv/pistol.acv",
                    "acv/ragdoll.acv",
                    "acv/rose_thorns_01.acv",
                    "acv/rose_thorns_02.acv",
                    "acv/set_you_free.acv",
                    "acv/snow_white.acv",
                    "acv/toes_in_the_ocean.acv",
                    "acv/wild_at_heart.acv",
                    "acv/window_warmth.acv"
                )
            )

            val gpuImage = GPUImage(itemView.context)
            gpuImage.setImage(bitmap)

            val gpuFilter = GPUImageToneCurveFilter()

            when (position) {
                1 -> gpuImage.setFilter(GPUImageGrayscaleFilter())
                else -> {
                    if (position > 1) {
                        val inputFilter = itemView.context.assets.open(filterFile[position - 2])
                        gpuFilter.setFromCurveFileInputStream(inputFilter)
                        inputFilter.close()

                        gpuImage.setFilter(gpuFilter)
                    }
                }
            }

            try {
                bitmap = gpuImage.bitmapWithFilterApplied
                itemView.apply {
                    thumbnails.setImageBitmap(bitmap)
                }
            } catch (e: NullPointerException) {
                //TODO something with get photo from Google Photos
            }

        }
    }
}
