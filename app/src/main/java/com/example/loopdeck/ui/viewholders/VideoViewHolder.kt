package com.example.loopdeck.ui.viewholders

import android.annotation.SuppressLint
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.data.MediaData
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    @SuppressLint("SimpleDateFormat")
    fun bind(
        mediaData: MediaData,
    ) {
        val uri = Uri.parse(mediaData.filePath)
        val bitmap = ThumbnailUtils.createVideoThumbnail(
            uri.toString(),
            MediaStore.Video.Thumbnails.MINI_KIND
        )
        val duration = getDuration(mediaData.filePath)
        val time = getDate(duration, "mm:ss")
        itemView.duration.text = time.toString()
        itemView.imageViewRecentVideo.setImageBitmap(bitmap)
    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getDuration(uri: String): Long {

        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(uri)
        val duration =
            java.lang.Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
        retriever.release()

        return duration
//    }
    }
}