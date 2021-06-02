package com.xorbix.loopdeck.editor.photoeditor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Typeface
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.IntRange
import androidx.annotation.RequiresPermission
import androidx.annotation.UiThread
import com.imagevideoeditor.R
import com.imagevideoeditor.photoeditor.BitmapUtil.removeTransparency
import java.io.File
import java.io.FileOutputStream
import java.util.*

@Suppress("UNREACHABLE_CODE")
class PhotoEditor private constructor(builder: Builder) : BrushViewChangeListener,
    MultiTouchListener.OnMultiTouchListener {
    private val mLayoutInflater: LayoutInflater
    private val context: Context
    private val parentView: PhotoEditorView?
    private val imageView: ImageView
    private val deleteView: View?
    private var framelayout: FrameLayout? = null
    private val brushDrawingView: BrushDrawingView?
    private val addedViews: MutableList<View?>
    private val redoViews: MutableList<View?>
    private var mOnPhotoEditorListener: OnPhotoEditorListener? = null
    private val isTextPinchZoomable: Boolean
    private val mDefaultTextTypeface: Typeface?
    private val mDefaultEmojiTypeface: Typeface?
    fun addImage(desiredImage: Bitmap) {
        val imageRootView = getLayout(ViewType.IMAGE)
        val imageView = imageRootView!!.findViewById<ImageView>(R.id.imgPhotoEditorImage)
        //        final FrameLayout frmBorder = imageRootView.findViewById(R.id.frmBorder);
//        final ImageView imgClose = imageRootView.findViewById(R.id.imgPhotoEditorClose);
        imageView.setImageBitmap(desiredImage)
        val multiTouchListener = multiTouchListener
        multiTouchListener.setOnGestureControl(object : MultiTouchListener.OnGestureControl {
            override fun onClick() {
//                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
//                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
//                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
//                frmBorder.setTag(!isBackgroundVisible);
            }

            override fun onLongClick() {

            }
        })
        multiTouchListener.setOnMultiTouchListener(this)
        imageRootView.setOnTouchListener(multiTouchListener)
        addViewToParent(imageRootView, ViewType.IMAGE)
    }
    /* */
    /**
     * This add the text on the [PhotoEditorView] with provided parameters
     * by default [TextView.setText] will be 18sp
     *
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    /*
    @SuppressLint("ClickableViewAccessibility")
    public void addText(String text, final int colorCodeTextView) {
        addText(null, text, colorCodeTextView);
    }*/
    /**
     * This add the text on the [PhotoEditorView] with provided parameters
     * by default [TextView.setText] will be 18sp
     *
     * @param textTypeface      typeface for custom font in the text
     * @param text              text to display
     * @param colorCodeTextView text color to be displayed
     */
    @SuppressLint("ClickableViewAccessibility")
    fun addText(textTypeface: Typeface?, text: String?, colorCodeTextView: Int, tag: Int) {
        val styleBuilder = TextStyleBuilder()
        styleBuilder.withTextColor(colorCodeTextView)
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface)
        }
        addText(text!!, styleBuilder, tag)
    }

    /**
     * This add the text on the [PhotoEditorView] with provided parameters
     * by default [TextView.setText] will be 18sp
     *
     * @param text         text to display
     * @param styleBuilder text style builder with your style
     */
    @SuppressLint("ClickableViewAccessibility")
    fun addText(text: Any, styleBuilder: TextStyleBuilder?, position: Int) {
        brushDrawingView!!.brushDrawingMode = false
        val textRootView = getLayout(ViewType.TEXT)
        val textInputTv = textRootView!!.findViewById<TextView>(R.id.tvPhotoEditorText)
        framelayout = textRootView.findViewById(R.id.framelayout)
        textInputTv.tag = position
        //        final ImageView imgClose = textRootView.findViewById(R.id.imgPhotoEditorClose);
//        final FrameLayout frmBorder = textRootView.findViewById(R.id.frmBorder);
        textInputTv.text = text as CharSequence?
        styleBuilder?.applyStyle(textInputTv)
        val multiTouchListener = multiTouchListener
        multiTouchListener.setOnGestureControl(object : MultiTouchListener.OnGestureControl {
            override fun onClick() {
                val textInput = textInputTv.text.toString()
                val currentTextColor = textInputTv.currentTextColor
                val position = textInputTv.tag as Int
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener!!.onEditTextChangeListener(
                        textRootView,
                        textInput,
                        currentTextColor,
                        position
                    )
                }
            }

            override fun onLongClick() {

            }
        })
        multiTouchListener.setOnMultiTouchListener(this)
        textRootView.setOnTouchListener(multiTouchListener)
        addViewToParent(textRootView, ViewType.TEXT)
    }
    /*   */
    /**
     * This will update text and color on provided view
     *
     * @param view      view on which you want update
     * @param inputText text to update [TextView]
     * @param colorCode color to update on [TextView]
     */
    /*
    public void editText(@NonNull View view, String inputText, @NonNull int colorCode) {
        editText(view, null, inputText, colorCode);
    }

    */
    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param textTypeface update typeface for custom font in the text
     * @param inputText    text to update [TextView]
     * @param colorCode    color to update on [TextView]
     */
    /*
    public void editText(@NonNull View view, @Nullable Typeface textTypeface, String inputText, @NonNull int colorCode) {
        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
        styleBuilder.withTextColor(colorCode);
        if (textTypeface != null) {
            styleBuilder.withTextFont(textTypeface);
        }

        editText(view, inputText, styleBuilder);
    }*/
    /**
     * This will update the text and color on provided view
     *
     * @param view         root view where text view is a child
     * @param inputText    text to update [TextView]
     * @param styleBuilder style to apply on [TextView]
     */
    fun editText(view: View, inputText: Any, styleBuilder: TextStyleBuilder?, position: Int) {
        val inputTextView = view.findViewById<TextView>(R.id.tvPhotoEditorText)
        inputTextView!!.tag = position
        if (inputTextView != null && addedViews.contains(view) && !TextUtils.isEmpty(inputText.toString())) {
            inputTextView.text = inputText as CharSequence?
            styleBuilder?.applyStyle(inputTextView)
            parentView!!.updateViewLayout(view, view.layoutParams)
            val i = addedViews.indexOf(view)
            if (i > -1) addedViews[i] = view
        }
    }

    fun addEmoji(emojiName: String?) {
        addEmoji(null, emojiName)
    }

    fun addEmoji(emojiTypeface: Typeface?, emojiName: String?) {
        brushDrawingView!!.brushDrawingMode = false
        val emojiRootView = getLayout(ViewType.EMOJI)
        val emojiTextView = emojiRootView!!.findViewById<TextView>(R.id.tvPhotoEditorText)
        //        final FrameLayout frmBorder = emojiRootView.findViewById(R.id.frmBorder);
//        final ImageView imgClose = emojiRootView.findViewById(R.id.imgPhotoEditorClose);
        if (emojiTypeface != null) {
            emojiTextView.typeface = emojiTypeface
        }
        emojiTextView.textSize = 56f
        emojiTextView.text = emojiName
        val multiTouchListener = multiTouchListener
        multiTouchListener.setOnGestureControl(object : MultiTouchListener.OnGestureControl {
            override fun onClick() {
//                boolean isBackgroundVisible = frmBorder.getTag() != null && (boolean) frmBorder.getTag();
//                frmBorder.setBackgroundResource(isBackgroundVisible ? 0 : R.drawable.rounded_border_tv);
//                imgClose.setVisibility(isBackgroundVisible ? View.GONE : View.VISIBLE);
//                frmBorder.setTag(!isBackgroundVisible);
            }

            override fun onLongClick() {}
        })
        multiTouchListener.setOnMultiTouchListener(this)
        emojiRootView.setOnTouchListener(multiTouchListener)
        addViewToParent(emojiRootView, ViewType.EMOJI)
    }

    /**
     * Add to root view from image,emoji and text to our parent view
     *
     * @param rootView rootview of image,text and emoji
     */
    private fun addViewToParent(rootView: View?, viewType: ViewType) {
        val params = RelativeLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)
        parentView!!.addView(rootView, params)
        addedViews.add(rootView)
        if (mOnPhotoEditorListener != null) mOnPhotoEditorListener!!.onAddViewListener(
            viewType,
            addedViews.size
        )
    }//multiTouchListener.setOnMultiTouchListener(this);

    /**
     * Create a new instance and scalable touchview
     *
     * @return scalable multitouch listener
     */
    private val multiTouchListener: MultiTouchListener
        private get() =//multiTouchListener.setOnMultiTouchListener(this);
            MultiTouchListener(
                deleteView,
                parentView!!,
                imageView,
                isTextPinchZoomable,
                mOnPhotoEditorListener
            )

    /**
     * Get root view by its type i.e image,text and emoji
     *
     * @param viewType image,text or emoji
     * @return rootview
     */
    private fun getLayout(viewType: ViewType): View? {
        var rootView: View? = null
        when (viewType) {
            ViewType.TEXT -> {
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null)
                val txtText = rootView.findViewById<TextView>(R.id.tvPhotoEditorText)
                if (txtText != null && mDefaultTextTypeface != null) {
                    txtText.gravity = Gravity.CENTER
                    if (mDefaultEmojiTypeface != null) {
                        txtText.typeface = mDefaultTextTypeface
                    }
                }
            }
            ViewType.IMAGE -> rootView =
                mLayoutInflater.inflate(R.layout.view_photo_editor_image, null)
            ViewType.EMOJI -> {
                rootView = mLayoutInflater.inflate(R.layout.view_photo_editor_text, null)
                val txtTextEmoji = rootView.findViewById<TextView>(R.id.tvPhotoEditorText)
                if (txtTextEmoji != null) {
                    if (mDefaultEmojiTypeface != null) {
                        txtTextEmoji.typeface = mDefaultEmojiTypeface
                    }
                    txtTextEmoji.gravity = Gravity.CENTER
                    txtTextEmoji.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                }
            }
        }
        if (rootView != null) {
            //We are setting tag as ViewType to identify what type of the view it is
            //when we remove the view from stack i.e onRemoveViewListener(ViewType viewType, int numberOfAddedViews);
            rootView.tag = viewType
            //            final ImageView imgClose = rootView.findViewById(R.id.imgPhotoEditorClose);
//            final View finalRootView = rootView;
//            if (imgClose != null) {
//                imgClose.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        viewUndo(finalRootView, viewType);
//                    }
//                });
//            }
        }
        return rootView
    }

    /**
     * Enable/Disable drawing mode to draw on [PhotoEditorView]
     *
     * @param brushDrawingMode true if mode is enabled
     */
    fun setBrushDrawingMode(brushDrawingMode: Boolean) {
        if (brushDrawingView != null) brushDrawingView.brushDrawingMode = brushDrawingMode
    }

    /**
     * @return true is brush mode is enabled
     */
    val brushDrawableMode: Boolean
        get() = brushDrawingView != null && brushDrawingView.brushDrawingMode

    /**
     * set opacity/transparency of brush while painting on [BrushDrawingView]
     *
     * @param opacity opacity is in form of percentage
     */
    fun setOpacity(@IntRange(from = 0, to = 100) opacity: Int) {
        var opacity = opacity
        if (brushDrawingView != null) {
            opacity = (opacity / 100.0 * 255.0).toInt()
            brushDrawingView.opacity = opacity
        }
    }

    /**
     * set the eraser size
     * <br></br>
     * **Note :** Eraser size is different from the normal brush size
     *
     * @param brushEraserSize size of eraser
     */
    fun setBrushEraserSize(brushEraserSize: Float) {
        brushDrawingView?.setBrushEraserSize(brushEraserSize)
    }

    fun setBrushEraserColor(@ColorInt color: Int) {
        brushDrawingView?.setBrushEraserColor(color)
    }

    val eraserSize: Float
        get() = (brushDrawingView?.eraserSize ?: 0) as Float

    /**
     * set the size of bursh user want to paint on canvas i.e [BrushDrawingView]
     *
     * @param size size of brush
     */
    var brushSize: Float
        get() = (brushDrawingView?.brushSize ?: 0) as Float
        set(size) {
            if (brushDrawingView != null) brushDrawingView.brushSize = size
        }

    /**
     * set brush color which user want to paint
     *
     * @param color color value for paint
     */
    var brushColor: Int
        get() = brushDrawingView?.brushColor ?: 0
        set(color) {
            if (brushDrawingView != null) brushDrawingView.brushColor = color
        }

    fun brushEraser() {
        brushDrawingView?.brushEraser()
    }

    /*private void viewUndo() {
        if (addedViews.size() > 0) {
            parentView.removeView(addedViews.remove(addedViews.size() - 1));
            if (mOnPhotoEditorListener != null)
                mOnPhotoEditorListener.onRemoveViewListener(addedViews.size());
        }
    }*/
    private fun viewUndo(removedView: View?, viewType: ViewType) {
        if (addedViews.size > 0) {
            if (addedViews.contains(removedView)) {
                parentView!!.removeView(removedView)
                addedViews.remove(removedView)
                redoViews.add(removedView)
                if (mOnPhotoEditorListener != null) {
                    mOnPhotoEditorListener!!.onRemoveViewListener(viewType, addedViews.size)
                }
            }
        }
    }

    fun undo(): Boolean {
        if (addedViews.size > 0) {
            val removeView = addedViews[addedViews.size - 1]
            if (removeView is BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.undo()
            } else {
                addedViews.removeAt(addedViews.size - 1)
                parentView!!.removeView(removeView)
                redoViews.add(removeView)
            }
            if (mOnPhotoEditorListener != null) {
                val viewTag = removeView!!.tag
                if (viewTag != null && viewTag is ViewType) {
                    mOnPhotoEditorListener!!.onRemoveViewListener(viewTag, addedViews.size)
                }
            }
        }
        return addedViews.size != 0
    }

    fun undoCanvas(): Boolean {
        return brushDrawingView != null && brushDrawingView.undo()
    }

    fun redo(): Boolean {
        if (redoViews.size > 0) {
            val redoView = redoViews[redoViews.size - 1]
            if (redoView is BrushDrawingView) {
                return brushDrawingView != null && brushDrawingView.redo()
            } else {
                redoViews.removeAt(redoViews.size - 1)
                parentView!!.addView(redoView)
                addedViews.add(redoView)
            }
            val viewTag = redoView!!.tag
            if (mOnPhotoEditorListener != null && viewTag != null && viewTag is ViewType) {
                mOnPhotoEditorListener!!.onAddViewListener(viewTag, addedViews.size)
            }
        }
        return redoViews.size != 0
    }

    fun clearBrushAllViews() {
        brushDrawingView?.clearAll()
    }

    /**
     * Removes all the edited operations performed [PhotoEditorView]
     * This will also clear the undo and redo stack
     */
    fun clearAllViews() {
        for (i in addedViews.indices) {
            parentView!!.removeView(addedViews[i])
        }
        if (addedViews.contains(brushDrawingView)) {
            parentView!!.addView(brushDrawingView)
        }
        addedViews.clear()
        redoViews.clear()
        clearBrushAllViews()
    }

    /**
     * Remove all helper boxes from views
     */
    @UiThread
    fun clearHelperBox() {
        for (i in 0 until parentView!!.childCount) {
            val childAt = parentView.getChildAt(i)
            //            FrameLayout frmBorder = childAt.findViewById(R.id.frmBorder);
//            if (frmBorder != null) {
//                frmBorder.setBackgroundResource(0);
//            }
//            ImageView imgClose = childAt.findViewById(R.id.imgPhotoEditorClose);
//            if (imgClose != null) {
//                imgClose.setVisibility(View.GONE);
//            }
        }
    }

    /**
     * Setup of custom effect using effect type and set parameters values
     *
     * @param customEffect [CustomEffect.Builder.setParameter]
     */
    fun setFilterEffect(customEffect: CustomEffect?) {
        parentView!!.setFilterEffect(customEffect)
    }

    fun setFilterEffect(filterType: PhotoFilter) {
        parentView!!.setFilterEffect(filterType)
    }

    override fun onEditTextClickListener(text: String?, colorCode: Int) {}
    override fun onRemoveViewListener(removedView: View?) {
        viewUndo(removedView, removedView!!.tag as ViewType)
    }

    /**
     * A callback to save the edited image asynchronously
     */
    interface OnSaveListener {
        /**
         * Call when edited image is saved successfully on given path
         *
         * @param imagePath path on which image is saved
         */
        fun onSuccess(imagePath: String)

        /**
         * Call when failed to saved image on given path
         *
         * @param exception exception thrown while saving image
         */
        fun onFailure(exception: Exception)
    }

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun saveAsFile(imagePath: String, onSaveListener: OnSaveListener) {
        saveAsFile(imagePath, SaveSettings.Builder().build(), onSaveListener)
    }

    /**
     * Save the edited image on given path
     *
     * @param imagePath      path on which image to be saved
     * @param saveSettings   builder for multiple save options [SaveSettings]
     * @param onSaveListener callback for saving image
     * @see OnSaveListener
     */
    @SuppressLint("StaticFieldLeak")
    @RequiresPermission(allOf = [Manifest.permission.WRITE_EXTERNAL_STORAGE])
    fun saveAsFile(
        imagePath: String,
        saveSettings: SaveSettings,
        onSaveListener: OnSaveListener
    ) {
        Log.d(TAG, "Image Path: $imagePath")
        parentView!!.saveFilter(object : OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                object : AsyncTask<String?, String?, Exception?>() {
                    override fun onPreExecute() {
                        super.onPreExecute()
                        clearHelperBox()
                        parentView.isDrawingCacheEnabled = false
                    }

                    override fun onPostExecute(e: Exception?) {
                        super.onPostExecute(e)
                        if (e == null) {
                            //Clear all views if its enabled in save settings
                            if (saveSettings.isClearViewsEnabled) clearAllViews()
                            onSaveListener.onSuccess(imagePath)
                        } else {
                            onSaveListener.onFailure(e)
                        }
                    }

                    override fun doInBackground(vararg p0: String?): Exception? {
                        val file = File(imagePath)
                        return try {
                            val out = FileOutputStream(file, false)
                            if (parentView != null) {
                                parentView.isDrawingCacheEnabled = true
                                val drawingCache =
                                    if (saveSettings.isTransparencyEnabled) removeTransparency(
                                        parentView.drawingCache
                                    ) else parentView.drawingCache
                                drawingCache.compress(
                                    saveSettings.compressFormat,
                                    saveSettings.compressQuality,
                                    out
                                )
                            }
                            out.flush()
                            out.close()
                            Log.d(TAG, "Filed Saved Successfully")
                            null
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Log.d(TAG, "Failed to save File")
                            e
                        }
                    }
                }.execute()
            }

            override fun onFailure(e: Exception?) {
                onSaveListener.onFailure(e!!)
            }
        })
    }

    /**
     * Save the edited image as bitmap
     *
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    fun saveAsBitmap(onSaveBitmap: OnSaveBitmap) {
        saveAsBitmap(SaveSettings.Builder().build(), onSaveBitmap)
    }

    /**
     * Save the edited image as bitmap
     *
     * @param saveSettings builder for multiple save options [SaveSettings]
     * @param onSaveBitmap callback for saving image as bitmap
     * @see OnSaveBitmap
     */
    @SuppressLint("StaticFieldLeak")
    fun saveAsBitmap(
        saveSettings: SaveSettings,
        onSaveBitmap: OnSaveBitmap
    ) {
        parentView!!.saveFilter(object : OnSaveBitmap {
            override fun onBitmapReady(saveBitmap: Bitmap?) {
                object : AsyncTask<String?, String?, Bitmap?>() {
                    override fun onPreExecute() {
                        super.onPreExecute()
                        clearHelperBox()
                        parentView.isDrawingCacheEnabled = false
                    }


                    override fun onPostExecute(bitmap: Bitmap?) {
                        super.onPostExecute(bitmap)
                        if (bitmap != null) {
                            if (saveSettings.isClearViewsEnabled) clearAllViews()
                            onSaveBitmap.onBitmapReady(bitmap)
                        } else {
                            onSaveBitmap.onFailure(Exception("Failed to load the bitmap"))
                        }
                    }

                    override fun doInBackground(vararg p0: String?): Bitmap? {
                        return run {
                            parentView.isDrawingCacheEnabled = true
                            if (saveSettings.isTransparencyEnabled) removeTransparency(parentView.drawingCache) else parentView.drawingCache
                        }
                        TODO("Not yet implemented")
                    }
                }.execute()
            }

            override fun onFailure(e: Exception?) {
                onSaveBitmap.onFailure(e)
            }
        })
    }

    /**
     * Callback on editing operation perform on [PhotoEditorView]
     *
     * @param onPhotoEditorListener [OnPhotoEditorListener]
     */
    fun setOnPhotoEditorListener(onPhotoEditorListener: OnPhotoEditorListener) {
        mOnPhotoEditorListener = onPhotoEditorListener
    }

    /**
     * Check if any changes made need to save
     *
     * @return true if nothing is there to change
     */
    val isCacheEmpty: Boolean
        get() = addedViews.size == 0 && redoViews.size == 0

    override fun onViewAdd(brushDrawingView: BrushDrawingView?) {
        if (redoViews.size > 0) {
            redoViews.removeAt(redoViews.size - 1)
        }
        addedViews.add(brushDrawingView)
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener!!.onAddViewListener(ViewType.BRUSH_DRAWING, addedViews.size)
        }
    }

    override fun onViewRemoved(brushDrawingView: BrushDrawingView?) {
        if (addedViews.size > 0) {
            val removeView = addedViews.removeAt(addedViews.size - 1)
            if (removeView !is BrushDrawingView) {
                parentView!!.removeView(removeView)
            }
            redoViews.add(removeView)
        }
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener!!.onRemoveViewListener(ViewType.BRUSH_DRAWING, addedViews.size)
        }
    }

    override fun onStartDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener!!.onStartViewChangeListener(ViewType.BRUSH_DRAWING)
        }
    }

    override fun onStopDrawing() {
        if (mOnPhotoEditorListener != null) {
            mOnPhotoEditorListener!!.onStopViewChangeListener(ViewType.BRUSH_DRAWING)
        }
    }


    class Builder(val context: Context, val parentView: PhotoEditorView) {
        val imageView: ImageView
        var deleteView: View? = null
        val brushDrawingView: BrushDrawingView
        var textTypeface: Typeface? = null
        var emojiTypeface: Typeface? = null

        //By Default pinch zoom on text is enabled
        var isTextPinchZoomable = true
        fun setDeleteView(deleteView: View?): Builder {
            this.deleteView = deleteView
            return this
        }

        fun setDefaultTextTypeface(textTypeface: Typeface?): Builder {
            this.textTypeface = textTypeface
            return this
        }

        fun setDefaultEmojiTypeface(emojiTypeface: Typeface?): Builder {
            this.emojiTypeface = emojiTypeface
            return this
        }

        fun setPinchTextScalable(isTextPinchZoomable: Boolean): Builder {
            this.isTextPinchZoomable = isTextPinchZoomable
            return this
        }

        /**
         * @return build PhotoEditor instance
         */
        fun build(): PhotoEditor {
            return PhotoEditor(this)
        }

        /**
         * Building a PhotoEditor which requires a Context and PhotoEditorView
         * which we have setup in our xml layout
         *
         * @param context         context
         * @param photoEditorView [PhotoEditorView]
         */
        init {
            imageView = parentView.source!!
            brushDrawingView = parentView.brushDrawingView!!
        }
    }

    companion object {
        private const val TAG = "PhotoEditor"
        private fun convertEmoji(emoji: String): String {
            val returnedEmoji: String
            returnedEmoji = try {
                val convertEmojiToInt = emoji.substring(2).toInt(16)
                String(Character.toChars(convertEmojiToInt))
            } catch (e: NumberFormatException) {
                ""
            }
            return returnedEmoji
        }

        /**
         * Provide the list of emoji in form of unicode string
         *
         * @param context context
         * @return list of emoji unicode
         */
        fun getEmojis(context: Context): ArrayList<String> {
            val convertedEmojiList = ArrayList<String>()
            val emojiList = context.resources.getStringArray(R.array.photo_editor_emoji)
            for (emojiUnicode in emojiList) {
                convertedEmojiList.add(convertEmoji(emojiUnicode))
            }
            return convertedEmojiList
        }
    }

    init {
        context = builder.context
        parentView = builder.parentView
        imageView = builder.imageView
        deleteView = builder.deleteView
        brushDrawingView = builder.brushDrawingView
        isTextPinchZoomable = builder.isTextPinchZoomable
        mDefaultTextTypeface = builder.textTypeface
        mDefaultEmojiTypeface = builder.emojiTypeface
        mLayoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        brushDrawingView.setBrushViewChangeListener(this)
        addedViews = ArrayList()
        redoViews = ArrayList()
    }
}