package com.example.loopdeck.ui.viewholders

import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.PublishData
import kotlinx.android.synthetic.main.item_recent_list_images.view.*
import kotlinx.android.synthetic.main.publish_row_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class PublishViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(
        publishData: PublishData,
    ) {

        val uri = Uri.parse(publishData.filePath)
        val bitmap = ThumbnailUtils.createVideoThumbnail(
            uri.toString(),
            MediaStore.Video.Thumbnails.MINI_KIND
        )

        val duration = getduration(publishData.filePath)
        val time = getDate(duration, "mm:ss")

        itemView.fileImage.setImageBitmap(bitmap)
        itemView.filename.text = publishData.name
        itemView.fileduration.text = time.toString()
    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getduration(uri: String): Long {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val duration =
            java.lang.Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        retriever.release()

        return duration
//    }
    }

}