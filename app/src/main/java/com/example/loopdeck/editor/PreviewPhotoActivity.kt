package com.example.loopdeck.editor

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
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaRepository
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.loopdeck.editor.filter.FilterListener
import com.example.loopdeck.editor.filter.FilterViewAdapter
import com.example.loopdeck.editor.fragments.AddMusicFragment
import com.example.loopdeck.editor.fragments.SoundPickerFragment
import com.example.loopdeck.editor.photoeditor.*
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import kotlinx.android.synthetic.main.activity_preview.*
import java.io.File
import java.io.IOException

@Suppress("UNREACHABLE_CODE")
class PreviewPhotoActivity : AppCompatActivity(), OnPhotoEditorListener,
    BrushArtFragment.BrushArtListener, View.OnClickListener, StickerBSFragment.StickerListener,
    FilterListener, BrushArtListener, SoundPickerFragment.SoundPickerListener,
    AddMusicFragment.AddMusicFragmentListener {

    var videoSurface: TextureView? = null
    var playListName: String? = null
    var ivImage: PhotoEditorView? = null
    var imgClose: ImageView? = null
    var imgDone: TextView? = null
    var imgDelete: ImageView? = null
    var imgDraw: ImageView? = null
    var imgText: ImageView? = null
    var imgUndo: ImageView? = null
    var imgSticker: ImageView? = null
    var imgAdMusic: ImageView? = null
    private val mFilterViewAdapter = FilterViewAdapter(this)
    var imgPath: String? = null
    private var masterImageFile: File? = null
    private val mConstraintSet = ConstraintSet()
    private var mIsFilterVisible = false
    private var mPhotoEditor: PhotoEditor? = null
    private val mPhotoEditorView: PhotoEditorView? = null
    private var brushArtFragment: BrushArtFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private lateinit var viewModel: CollectionViewModel

    val soundPickerFragment = SoundPickerFragment.newInstance(
        soundPickerListener = this,
        musicListener = this,
        selectForVideo = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_preview)
        viewModel = activityViewModelProvider()
        initViews()


        imgPath = intent.getStringExtra("imagePath")
        playListName = intent.getStringExtra("playlistName")
        masterImageFile = File(imgPath)

        //        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
        ivImage!!.source?.let { Glide.with(this).load(imgPath).into(it) }
        //        Glide.with(this).load(R.drawable.trans).into(binding.ivImage.getSource());
    }

    private fun initViews() {
        videoSurface = findViewById(R.id.videoSurface)
        ivImage = findViewById(R.id.ivImage)
        imgClose = findViewById(R.id.imgClose)
        imgDone = findViewById(R.id.imgDone)
        imgDraw = findViewById(R.id.iconBrushes)
        imgText = findViewById(R.id.iconText)
        imgSticker = findViewById(R.id.imgSticker)
        imgAdMusic = findViewById(R.id.imgAddmusic)
        mStickerBSFragment = StickerBSFragment()
        mStickerBSFragment!!.setStickerListener(this)
        brushArtFragment = BrushArtFragment()
        brushArtFragment!!.setBrushArtListener(this)
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
        imgSticker!!.setOnClickListener(this)
        imgAdMusic!!.setOnClickListener(this)

        imgAddmusic
        if (mPhotoEditor!!.undoCanvas()) {
        }
        val llmFilters = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvFilterView!!.layoutManager = llmFilters
        rvFilterView!!.adapter = mFilterViewAdapter


        iconFilters.setOnClickListener {
            main_menu.visibility = View.GONE
            layoutfilter.visibility = View.VISIBLE
            showFilter(true)
        }

        done.setOnClickListener {
            main_menu.visibility = View.VISIBLE
            layoutfilter.visibility = View.GONE
            showFilter(false)
        }
        close.setOnClickListener {
            main_menu.visibility = View.VISIBLE
            layoutfilter.visibility = View.GONE
            showFilter(false)
        }

    }

    override fun onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false)
            main_menu.visibility = View.VISIBLE
            layoutfilter.visibility = View.GONE
        } else {
            super.onBackPressed()
        }

    }

    override fun onClick(v: View) {
        if (R.id.imgClose == v.id) onBackPressed()
        else if (R.id.imgDone == v.id) saveImage()
        else if (R.id.iconBrushes == v.id) setDrawingMode()
        else if (R.id.iconText == v.id) {
            mPhotoEditor!!.setBrushDrawingMode(false)
            val textEditorDialogFragment = TextEditorDialogFragment.show(this, 0)
            textEditorDialogFragment.setOnTextEditorListener(object :
                TextEditorDialogFragment.TextEditor {
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
//        } else if (R.id.imgUndo == v.id)
//            mPhotoEditor!!.clearBrushAllViews()
        } else if (R.id.imgSticker == v.id) {
            mPhotoEditor!!.setBrushDrawingMode(false)
            mStickerBSFragment!!.show(
                supportFragmentManager, mStickerBSFragment!!.tag
            )
        } else if (R.id.imgAddmusic == v.id) {
            mPhotoEditor!!.setBrushDrawingMode(false)

            val timeInMillis =
                10L * 1000L//OptiUtils.getVideoDuration(applicationContext, masterImageFile!!)
            soundPickerFragment.setImageFilePath(masterImageFile!!)
            soundPickerFragment.setDuartion(timeInMillis)
            showBottomSheetDialogFragment(soundPickerFragment)

        }

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

    private fun showBottomSheetDialogFragment(bottomSheetDialogFragment: BottomSheetDialogFragment) {
        val bundle = Bundle()
        bottomSheetDialogFragment.arguments = bundle
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun setDrawingMode() {

        mPhotoEditor!!.setBrushDrawingMode(true)
        imgDraw!!.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp))
        brushArtFragment!!.show(supportFragmentManager, brushArtFragment!!.tag)
//        if (mPhotoEditor!!.brushDrawableMode) {
//            mPhotoEditor!!.setBrushDrawingMode(false)
//            imgDraw!!.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp))
//        } else {
//            mPhotoEditor!!.setBrushDrawingMode(true)
//            imgDraw!!.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary))
//            propertiesBSFragment!!.show(supportFragmentManager, propertiesBSFragment!!.tag)
//        }
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
        super.onActivityResult(requestCode, resultCode, data)
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

    private fun showFilter(isVisible: Boolean) {
        mPhotoEditor!!.setBrushDrawingMode(false)
        mIsFilterVisible = isVisible
        mConstraintSet.clone(mRootView)
        if (isVisible) {
            mConstraintSet.clear(rvFilterView!!.id, ConstraintSet.START)
            mConstraintSet.connect(
                rvFilterView!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.START
            )
            mConstraintSet.connect(
                rvFilterView!!.id, ConstraintSet.END,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
        } else {
            mConstraintSet.connect(
                rvFilterView!!.id, ConstraintSet.START,
                ConstraintSet.PARENT_ID, ConstraintSet.END
            )
            mConstraintSet.clear(rvFilterView!!.id, ConstraintSet.END)
        }
        val changeBounds = android.transition.ChangeBounds()
        changeBounds.duration = 350
        changeBounds.interpolator = AnticipateOvershootInterpolator(1.0f)
        android.transition.TransitionManager.beginDelayedTransition(mRootView!!, changeBounds)
        mConstraintSet.applyTo(mRootView)
    }

    override fun onStickerClick(bitmap: Bitmap?) {
        mPhotoEditor!!.setBrushDrawingMode(false)
        imgSticker!!.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp))
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
            TextEditorDialogFragment.TextEditor {

            override fun onDone(inputText: String?, colorCode: Int, position: Int) {
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

    override fun onBrushArtColorChanged(colorCode: Int) {
        mPhotoEditor!!.brushColor = colorCode
    }

    override fun onBrushArtOpacityChanged(opacity: Int) {
        mPhotoEditor!!.setOpacity(opacity)
    }

    override fun onBrushArtSizeChanged(brushSize: Int) {
        mPhotoEditor!!.brushSize = brushSize.toFloat()
    }

    companion object {
        private val TAG = PreviewPhotoActivity::class.java.simpleName
        private val CAMERA_REQUEST = 52
        private val PICK_REQUEST = 53
    }

    override fun onFilterSelected(photoFilter: PhotoFilter) {
        mPhotoEditor!!.setFilterEffect(photoFilter)
    }

    override fun onBrushArtEraserClicked() {
        mPhotoEditor!!.brushEraser()
    }

    override fun onDismissSoundPicker() {

    }

    override fun onSuccessAddMusic(file: File) {
        val intent = Intent(this, PreviewVideoActivity::class.java)
        intent.putExtra("videoPath", file.absolutePath)
        startActivity(intent)
        finish()
    }

    override fun OnErrorAddMusic() {
        Toast.makeText(this, "Adding music to image failed", Toast.LENGTH_SHORT).show()
    }


}