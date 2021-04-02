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
import java.util.concurrent.TimeUnit

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
//        val _duration: Long
////        _duration = getduration(uri.toString())
//
//        val milliseconds: Long = _duration
//// long minutes = (milliseconds / 1000) / 60;
//        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
//// long seconds = (milliseconds / 1000);
//        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

//        itemView.duration.setText("${minutes}:${seconds}")
        itemView.imageViewRecentVideo.setImageBitmap(bitmap)
    }

//    fun getduration(uri: String): Long {
//        val retriever = MediaMetadataRetriever()
//        retriever.setDataSource(uri)
//        val duration =
//            java.lang.Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
//        retriever.release()
//
//        return duration
//    }
}