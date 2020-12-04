package com.imagevideoeditor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.TextureView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.imagevideoeditor.TextEditorDialogFragment.TextEditor
import com.imagevideoeditor.databinding.ActivityPreviewBinding
import com.imagevideoeditor.photoeditor.*
import kotlinx.android.synthetic.main.activity_preview_video.*
import java.io.File
import java.io.IOException

@Suppress("UNREACHABLE_CODE")
abstract class PreviewPhotoActivity() : AppCompatActivity(), OnPhotoEditorListener,
    PropertiesBSFragment.Properties, View.OnClickListener, StickerBSFragment.StickerListener {

    var videoSurface: TextureView? = null
    var ivImage: PhotoEditorView? = null
    var imgClose: ImageView? = null
    var imgDone: ImageView? = null
    var imgDelete: ImageView? = null
    var imgDraw: ImageView? = null
    var imgText: ImageView? = null
    var imgUndo: ImageView? = null
    var imgSticker: ImageView? = null
    private var binding: ActivityPreviewBinding? = null
    private var mPhotoEditor: PhotoEditor? = null
    private val mPhotoEditorView: PhotoEditorView? = null
    private var propertiesBSFragment: PropertiesBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview)
        initViews()
        //        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        ivImage!!.source?.let { Glide.with(this).load(intent.getStringExtra("DATA")).into(it) }
        //        Glide.with(this).load(R.drawable.trans).into(binding.ivImage.getSource());
    }

    private fun initViews() {
        videoSurface = findViewById(R.id.videoSurface)
        ivImage = findViewById(R.id.ivImage)
        imgClose = findViewById(R.id.imgClose)
        imgDone = findViewById(R.id.imgDone)
        imgDelete = findViewById(R.id.imgDelete)
        imgDraw = findViewById(R.id.imgDraw)
        imgText = findViewById(R.id.imgText)
        imgUndo = findViewById(R.id.imgUndo)
        imgSticker = findViewById(R.id.imgSticker)
        mStickerBSFragment = StickerBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        propertiesBSFragment = PropertiesBSFragment()
        propertiesBSFragment!!.setPropertiesChangeListener(this)
        mPhotoEditor = PhotoEditor.Builder(this, ivImage!!)
            .setPinchTextScalable(true) // set flag to make text scalable when pinch
            .setDeleteView(imgDelete) //.setDefaultTextTypeface(mTextRobotoTf)
            //.setDefaultEmojiTypeface(mEmojiTypeFace)
            .build() // build photo editor sdk
        mPhotoEditor!!.setOnPhotoEditorListener(this)
        imgClose!!.setOnClickListener(this)
        imgDone!!.setOnClickListener(this)
        imgDraw!!.setOnClickListener(this)
        imgText!!.setOnClickListener(this)
        imgUndo!!.setOnClickListener(this)
        imgSticker!!.setOnClickListener(this)
        if (mPhotoEditor!!.undoCanvas()) {
        }
    }

    override fun onClick(v: View) {
        if (R.id.imgClose == v.id) onBackPressed()
        else if (R.id.imgDone == v.id) saveImage()
        else if (R.id.imgDraw == v.id) setDrawingMode()
        else if (R.id.imgText == v.id) {
            val textEditorDialogFragment = TextEditorDialogFragment.show(this, 0)
            textEditorDialogFragment.setOnTextEditorListener(object : TextEditor {
                override fun onDone(inputText: String?, colorCode: Int, position: Int) {
                    val styleBuilder = TextStyleBuilder()
                    styleBuilder.withTextColor(colorCode)
                    val typeface =
                        TextEditorDialogFragment.getDefaultFontIds(applicationContext)
                            ?.get(position)?.let {
                                ResourcesCompat.getFont(
                                    applicationContext,
                                    it
                                )
                            }
                    styleBuilder.withTextFont((typeface)!!)
                    mPhotoEditor!!.addText(inputText!!, styleBuilder, position)
                }
            })
        } else if (R.id.imgUndo == v.id)
            mPhotoEditor!!.clearBrushAllViews()
        else if (R.id.imgSticker == v.id) mStickerBSFragment!!.show(
            supportFragmentManager, mStickerBSFragment!!.tag
        )
        //        switch (v.getId()) {
//            case R.id.imgClose:
//                onBackPressed();
//                break;
//            case R.id.imgDone:
//                saveImage();
//                break;
//            case R.id.imgDraw:
//                setDrawingMode();
//                break;
//            case R.id.imgText:
//                TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this,0);
//                textEditorDialogFragment.setOnTextEditorListener(new TextEditorDialogFragment.TextEditor() {
//
//                    @Override
//                    public void onDone(String inputText, int colorCode,int position) {
//                        final TextStyleBuilder styleBuilder = new TextStyleBuilder();
//                        styleBuilder.withTextColor(colorCode);
//                        Typeface typeface = ResourcesCompat.getFont(PreviewPhotoActivity.this,TextEditorDialogFragment.getDefaultFontIds(PreviewPhotoActivity.this).get(position));
//                        styleBuilder.withTextFont(typeface);
//                        mPhotoEditor.addText(inputText, styleBuilder,position);
//                    }
//                });
//                break;
//            case R.id.imgUndo:
//                Log.d("canvas>>",mPhotoEditor.undoCanvas()+"");
//                mPhotoEditor.clearBrushAllViews();
//                break;
//            case R.id.imgSticker:
//                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
//                break;
//
//        }
    }

    private fun setDrawingMode() {
        if (mPhotoEditor!!.brushDrawableMode) {
            mPhotoEditor!!.setBrushDrawingMode(false)
            imgDraw!!.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp))
        } else {
            mPhotoEditor!!.setBrushDrawingMode(true)
            imgDraw!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
            propertiesBSFragment!!.show(supportFragmentManager, propertiesBSFragment!!.tag)
        }
    }

    @SuppressLint("MissingPermission")
    private fun saveImage() {
        val file = File(
            (Environment.getExternalStorageDirectory()
                .toString() + File.separator + ""
                    + System.currentTimeMillis() + ".png")
        )
        try {
            file.createNewFile()
            val saveSettings = SaveSettings.Builder()
                .setClearViewsEnabled(true)
                .setTransparencyEnabled(false)
                .build()
            mPhotoEditor!!.saveAsFile(
                file.absolutePath,
                saveSettings,
                object : PhotoEditor.OnSaveListener {
                    override fun onSuccess(imagePath: String) {
                        ivImage!!.source?.setImageURI(Uri.fromFile(File(imagePath)))
                        Toast.makeText(
                            this@PreviewPhotoActivity,
                            "Saved successfully...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailure(exception: Exception) {
                        Toast.makeText(
                            this@PreviewPhotoActivity,
                            "Saving Failed...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST -> {
                    mPhotoEditor!!.clearAllViews()
                    val photo = data!!.extras!!["data"] as Bitmap?
                    mPhotoEditorView!!.source?.setImageBitmap(photo)
                }
                PICK_REQUEST -> try {
                    mPhotoEditor!!.clearAllViews()
                    val uri = data!!.data
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver, uri
                    )
                    mPhotoEditorView!!.source?.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onStickerClick(bitmap: Bitmap?) {
        mPhotoEditor!!.setBrushDrawingMode(false)
        binding!!.imgDraw.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp))
        mPhotoEditor!!.addImage(bitmap)
    }

    override fun onEditTextChangeListener(
        rootView: View?,
        text: String?,
        colorCode: Int,
        position: Int
    ) {
        val textEditorDialogFragment =
            TextEditorDialogFragment.show(this, (text)!!, colorCode, position)
        textEditorDialogFragment.setOnTextEditorListener(object :
            TextEditor {

            override fun onDone(inputText: String?, colorCode: Int, position: Int) {
                TODO("Not yet implemented")
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                val typeface =
                    TextEditorDialogFragment.getDefaultFontIds(this@PreviewPhotoActivity)
                        ?.get(position)?.let {
                            ResourcesCompat.getFont(
                                this@PreviewPhotoActivity,
                                it
                            )
                        }
                styleBuilder.withTextFont((typeface)!!)
                mPhotoEditor!!.editText((rootView)!!, inputText!!, styleBuilder, position)
            }
        })
    }

    override fun onAddViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onAddViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onRemoveViewListener(viewType: ViewType?, numberOfAddedViews: Int) {
        Log.d(
            TAG,
            "onRemoveViewListener() called with: viewType = [$viewType], numberOfAddedViews = [$numberOfAddedViews]"
        )
    }

    override fun onStartViewChangeListener(viewType: ViewType?) {
        Log.d(TAG, "onStartViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onStopViewChangeListener(viewType: ViewType?) {
        Log.d(TAG, "onStopViewChangeListener() called with: viewType = [$viewType]")
    }

    override fun onColorChanged(colorCode: Int) {
        mPhotoEditor!!.brushColor = colorCode
    }

    override fun onOpacityChanged(opacity: Int) {}
    override fun onBrushSizeChanged(brushSize: Int) {}

    companion object {
        private val TAG = PreviewPhotoActivity::class.java.simpleName
        private val CAMERA_REQUEST = 52
        private val PICK_REQUEST = 53
    }
}