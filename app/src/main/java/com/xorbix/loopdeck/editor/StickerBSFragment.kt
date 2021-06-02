package com.xorbix.loopdeck.editor

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.xorbix.loopdeck.R
import com.xorbix.loopdeck.editor.api.ApiClient
import com.xorbix.loopdeck.editor.api.SearchApi
import com.xorbix.loopdeck.editor.entities.ItemEntity
import com.xorbix.loopdeck.editor.entities.SearchEntity
import com.xorbix.loopdeck.progressbar.CustomProgressDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StickerBSFragment : BottomSheetDialogFragment() {

    private var items = mutableListOf<ItemEntity>()

    private val progressDialog = CustomProgressDialog()

    val stickerAdapter = StickerAdapter()
    var mStickerListener: StickerListener? = null
    fun setStickerListener(stickerListener: StickerListener?) {
        mStickerListener = stickerListener
    }

    val searchApi: SearchApi by lazy {
        ApiClient.getSearchApi()
    }

    var editTextSearch: EditText? = null

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

        val searchEntities: Call<SearchEntity> = searchApi.getSearchResult(query = "sticker")
        searchEntities.enqueue(callback)

        val params = (contentView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }
        editTextSearch = contentView.findViewById(R.id.stickersearch)

        editTextSearch?.setOnEditorActionListener { textView, _, _ ->
            val q = textView.text.toString()
            val searchEntity = searchApi
                .getSearchResult(q, "vector", "all")
            searchEntity.enqueue(callback)
            false
        }

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
            setThumbnailImage(entity.previewURL, holder.imgSticker)

            holder.imgSticker.setOnClickListener {
                downloadViaPicasso(items[position])
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var imgSticker: ImageView

            init {
                imgSticker = itemView.findViewById(R.id.imgSticker)
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


    private fun downloadViaPicasso(sticker: ItemEntity) {

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                Toast.makeText(context, "Download Started", Toast.LENGTH_SHORT).show()
                progressDialog.show(context!!, "Downloading please Wait...")
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
                Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show()
                progressDialog.dialog.dismiss()
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                progressDialog.dialog.dismiss()
                mStickerListener?.let {
                    it.onStickerClick(bitmap)
                    dismiss()
                }
            }

        }
        Picasso.with(requireContext()).load(sticker.webformatURL).into(target)

    }
}