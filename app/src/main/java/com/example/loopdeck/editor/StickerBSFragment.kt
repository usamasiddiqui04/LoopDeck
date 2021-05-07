package com.example.loopdeck.editor

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.R
import com.example.loopdeck.editor.Utils.StringConverter
import com.example.loopdeck.editor.api.Fabric
import com.example.loopdeck.editor.api.SearchApi
import com.example.loopdeck.editor.entities.ItemEntity
import com.example.loopdeck.editor.entities.SearchEntity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_sticker_emoji_dialog.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StickerBSFragment() : BottomSheetDialogFragment() {

    private var items: ArrayList<ItemEntity>? = null
    var progressDialog: ProgressDialog? = null
    val stickerAdapter = StickerAdapter()
    var refid: Long? = null
    var mStickerListener: StickerListener? = null
    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }
    var searchStickers: EditText? = null

    interface StickerListener {
        fun onStickerClick(bitmap: Bitmap?)
    }

    private val mBottomSheetBehaviorCallback: BottomSheetBehavior.BottomSheetCallback =
        object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_sticker_emoji_dialog, null)
        dialog.setContentView(contentView)

        items = ArrayList()
        progressDialog = ProgressDialog(context)


        var query = "vectors"
        query = StringConverter.getQueryString(query)

        var type = "Vector graphics"
        type = StringConverter.getImageTypeQuery(type)


        var orientation = "Any orientation"
        orientation = StringConverter.getImageOrientationQuery(orientation)

        val searchApi: SearchApi = Fabric.getSearchApi()
        val searchEntities: Call<SearchEntity> = searchApi
            .getSearchResult(query, type, orientation)
        searchEntities.enqueue(callback)

        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        searchStickers = contentView.findViewById(R.id.stickersearch)
        (contentView.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        val rvEmoji: RecyclerView = contentView.findViewById(R.id.rvEmoji)
        val gridLayoutManager = GridLayoutManager(activity, 4)
        rvEmoji.layoutManager = gridLayoutManager
        rvEmoji.adapter = stickerAdapter

    }

    private val callback: Callback<SearchEntity?> = object : Callback<SearchEntity?> {
        override fun onResponse(call: Call<SearchEntity?>, response: Response<SearchEntity?>) {
            if (response.isSuccessful) {
                val result = response.body()
                if (result != null) {
                    items = result.items
                    if (items != null && items!!.size > 0) {

                        stickerAdapter.notifyDataSetChanged()

                    } else {
                    }
                } else {
                    showErrorToast()
                }
            } else {
                showErrorToast()
            }
        }

        override fun onFailure(call: Call<SearchEntity?>, t: Throwable) {
            t.printStackTrace()
            showErrorToast()
        }
    }

    inner class StickerAdapter() : RecyclerView.Adapter<StickerAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.row_sticker, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val entity = items!![position]
            setThumbnailImage(entity.webformatURL, holder.imgSticker)

            holder.imgSticker.setOnClickListener {
                downloadImage(position)
            }
        }

        override fun getItemCount(): Int {
            return items!!.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var imgSticker: ImageView

            init {
                imgSticker = itemView.findViewById(R.id.imgSticker)
//                itemView.setOnClickListener(View.OnClickListener {
//
//                    downloadImage()
//                    if (mStickerListener != null) {
//                        mStickerListener!!.onStickerClick(
//                            BitmapFactory.decodeResource(
//                                resources,
//                                items!![adapterPosition].webformatURL.toInt()
//                            )
//                        )
//                    }
//                    dismiss()
//                })
            }
        }

        fun setThumbnailImage(url: String, thumbnailView: ImageView) {
            Picasso.with(thumbnailView.context)
                .load(url)
                .into(thumbnailView)
        }
    }

    private fun convertEmoji(emoji: String): String {
        var returnedEmoji = ""
        try {
            val convertEmojiToInt = emoji.substring(2).toInt(16)
            returnedEmoji = getEmojiByUnicode(convertEmojiToInt)
        } catch (e: NumberFormatException) {
            returnedEmoji = ""
        }
        return returnedEmoji
    }

    private fun getEmojiByUnicode(unicode: Int): String {
        return String(Character.toChars(unicode))
    }

    private fun showErrorToast() {
        Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
    }

    private fun downloadImage(position: Int) {
        val url = items!![position].webformatURL
        val name = StringConverter.getImageNameFromUrl(url)
        val downloadManager = activity!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI
                    or DownloadManager.Request.NETWORK_MOBILE
        )
            .setAllowedOverRoaming(false)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
        downloadManager?.enqueue(request) ?: showErrorToast()


    }


    inner class DownloadBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action: String? = intent.getAction()
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                Toast.makeText(context, "Download Complete", Toast.LENGTH_SHORT).show()
            }
        }
    }
}