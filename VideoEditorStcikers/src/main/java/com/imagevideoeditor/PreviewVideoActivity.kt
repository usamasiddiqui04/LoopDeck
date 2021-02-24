package com.imagevideoeditor

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import com.daasuu.epf.EPlayerView
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.Utils.DimensionData
import com.imagevideoeditor.Utils.Utils
import com.imagevideoeditor.filter.EditVideoActivity
import com.imagevideoeditor.filter.FilterVideoFragment
import com.imagevideoeditor.filter.interfaces.AddFilterListener
import com.imagevideoeditor.filter.interfaces.FilterVideoCallBack
import com.imagevideoeditor.filter.utils.FilterType
import com.imagevideoeditor.fragments.SoundPickerFragment
import com.imagevideoeditor.photoeditor.*
import com.obs.marveleditor.fragments.OptiBaseCreatorDialogFragment
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import com.obs.marveleditor.utils.OptiUtils
import kotlinx.android.synthetic.main.activity_preview_video.*
import java.io.File
import java.io.IOException
import java.util.*

private val displayMetrics1 = DisplayMetrics()

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PreviewVideoActivity : AppCompatActivity(), OnPhotoEditorListener, OptiFFMpegCallback,
    PropertiesBSFragment.Properties, View.OnClickListener, StickerBSFragment.StickerListener,
    OptiBaseCreatorDialogFragment.CallBacks, AddFilterListener {
    var videoSurface: FrameLayout? = null
    var ivImage: PhotoEditorView? = null
    var imgClose: ImageView? = null
    var imgDone: ImageView? = null
    private var masterVideoFile: File? = null
    private var tagName: String = PreviewVideoActivity::class.java.simpleName
    var imgDelete: ImageView? = null
    var imgDraw: ImageView? = null
    var imgText: ImageView? = null
    var imgUndo: ImageView? = null
    var imgSticker: ImageView? = null
    var imgTrim: ImageView? = null
    var imgChanesound: ImageView? = null
    private var mPhotoEditor: PhotoEditor? = null
    private val globalVideoUrl = ""
    private var propertiesBSFragment: PropertiesBSFragment? = null
    private var mStickerBSFragment: StickerBSFragment? = null
    private var mediaPlayer: MediaPlayer? = null
    private var videoPath: String? = null
    private var imagePath: String? = null
    private var exeCmd: ArrayList<String>? = null
    private var fFmpeg: FFmpeg? = null
    private lateinit var newCommand: Array<String?>
    private var progressDialog: ProgressDialog? = null
    private var originalDisplayWidth = 0
    private var originalDisplayHeight = 0
    private var newCanvasWidth = 0
    private var newCanvasHeight = 0
    private var DRAW_CANVASW = 0
    private var DRAW_CANVASH = 0
    private lateinit var mAppName: String
    private lateinit var mAppPath: File
    private var filtervideocallback: FilterVideoCallBack? = null
    lateinit var player: SimpleExoPlayer
    lateinit var ePlayerView: EPlayerView

    private lateinit var filename: String
    private lateinit var filterFilepath: String

    private var mPosition: Int = 0

    private val onCompletionListener =
        MediaPlayer.OnCompletionListener { mediaPlayer -> mediaPlayer.start() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_preview_video)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        //        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview_video);
        videoPath = intent!!.getStringExtra("videoPath")
        masterVideoFile = File(videoPath)
        initViews()
        //        Drawable transparentDrawable = new ColorDrawable(Color.TRANSPARENT);
//        Glide.with(this).load(getIntent().getStringExtra("DATA")).into(binding.ivImage.getSource());
        ivImage!!.source?.let { Glide.with(this).load(R.drawable.trans).centerCrop().into(it) }
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(videoPath)
        val metaRotation =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
        val rotation = metaRotation?.toInt() ?: 0
        if (rotation == 90 || rotation == 270) {
            DRAW_CANVASH =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            DRAW_CANVASW =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        } else {
            DRAW_CANVASW =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            DRAW_CANVASH =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        }
        setCanvasAspectRatio()
        videoSurface!!.layoutParams.width = newCanvasWidth
        videoSurface!!.layoutParams.height = newCanvasHeight
//        ivImage!!.layoutParams.width = newCanvasWidth
//        ivImage!!.layoutParams.height = newCanvasHeight
        Log.d(
            ">>",
            "width>> " + newCanvasWidth + "height>> " + newCanvasHeight + " rotation >> " + rotation
        )
    }


    override fun onResume() {
        super.onResume()
        setUpSimpleExoPlayer()
        setUpGlPlayerView()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun releasePlayer() {
        ePlayerView.onPause()
        player.stop()
        player.release()
    }

    private fun setUpGlPlayerView() {
        ePlayerView = EPlayerView(applicationContext).apply {
            setSimpleExoPlayer(player)
        }
        videoSurface!!.addView(ePlayerView)
        ePlayerView.onResume()
    }

    private fun setUpSimpleExoPlayer() {

        val dataSourceFactory = DefaultDataSourceFactory(
            applicationContext,
            Util.getUserAgent(applicationContext, mAppName)
        )
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.fromFile(masterVideoFile))

        player = ExoPlayerFactory.newSimpleInstance(applicationContext).apply {
            prepare(videoSource)
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    private fun initViews() {
        mAppName = getString(R.string.app_name)
        mAppPath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            mAppName
        )

//        filepath = arguments?.getString(ARG_KEY_URI) ?: ""
        filename =
            masterVideoFile!!.toString().substring(masterVideoFile.toString().lastIndexOf("/") + 1)
        filterFilepath = "$mAppPath/MP4_$filename"

        videoSurface = findViewById(R.id.videoSurface)
        ivImage = findViewById(R.id.ivImage)
        imgClose = findViewById(R.id.imgClose)
        imgDone = findViewById(R.id.imgDone)
        imgDelete = findViewById(R.id.imgDelete)
        imgDraw = findViewById(R.id.imgdraw2)
        imgText = findViewById(R.id.imgText)
        imgUndo = findViewById(R.id.imgUndo)
//        imgTrim = findViewById(R.id.imgTrim)
        imgChanesound = findViewById(R.id.changesound)

        imgSticker = findViewById(R.id.imgSticker)
        fFmpeg = FFmpeg.getInstance(this)
        progressDialog = ProgressDialog(this)
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
        imgClose?.setOnClickListener(this)
        imgDone?.setOnClickListener(this)
        imgDraw?.setOnClickListener(this)
        imgText?.setOnClickListener(this)
        imgUndo?.setOnClickListener(this)
        imgSticker?.setOnClickListener(this)
        imgTrim?.setOnClickListener(this)
        imgChanesound?.setOnClickListener(this)
//        imgPlayback?.setOnClickListener(this)
        imgAddmusic?.setOnClickListener(this)
        exeCmd = ArrayList()
        try {
            fFmpeg?.loadBinary(object : FFmpegLoadBinaryResponseHandler {
                override fun onFailure() {
                    Log.d("binaryLoad", "onFailure")
                }

                override fun onSuccess() {
                    Log.d("binaryLoad", "onSuccess")
                }

                override fun onStart() {
                    Log.d("binaryLoad", "onStart")
                }

                override fun onFinish() {
                    Log.d("binaryLoad", "onFinish")
                }
            })
        } catch (e: FFmpegNotSupportedException) {
            e.printStackTrace()
        }
    }


    override fun onDidNothing() {

    }

    override fun onFileProcessed(file: File) {
        masterVideoFile = file
//        mediaPlayer?.release()
//        initializePlayer()
        releasePlayer()

    }

    override fun getFile(): File? {
        return masterVideoFile
    }

    override fun reInitPlayer() {
    }

    override fun onAudioFileProcessed(convertedAudioFile: File) {
        TODO("Not yet implemented")
    }

    override fun showLoading(isShow: Boolean) {


        if (isShow && progressDialog?.isShowing == false) {
            progressDialog?.apply {
                setTitle("Processing Your Video")
                setMessage("Please Wait")
                setCanceledOnTouchOutside(false)
                show()
            }
//            progressbar.visibility = View.VISIBLE
//            progressbar.show()
        } else {
            progressDialog?.dismiss()
//            progressbar.visibility = View.INVISIBLE
//            progressbar.hide()
        }
    }


    override fun openGallery() {
        TODO("Not yet implemented")
    }

    override fun openCamera() {
        TODO("Not yet implemented")
    }

    private fun executeCommand(command: Array<String?>?, absolutePath: String?) {
        try {
            fFmpeg!!.execute(command, object : FFmpegExecuteResponseHandler {
                override fun onSuccess(s: String) {
                    Log.d("CommandExecute", "onSuccess  $s")
                    Toast.makeText(applicationContext, "Sucess", Toast.LENGTH_SHORT).show()
                    val i = Intent(this@PreviewVideoActivity, VideoPreviewActivity::class.java)
                    i.putExtra("DATA", absolutePath)
                    startActivity(i)
                }

                override fun onProgress(s: String) {
                    progressDialog!!.setMessage(s)
                    Log.d("CommandExecute", "onProgress  $s")
                }

                override fun onFailure(s: String) {
                    Log.d("CommandExecute", "onFailure  $s")
                    progressDialog!!.hide()
                }

                override fun onStart() {
                    progressDialog!!.setTitle("Preccesing")
                    progressDialog!!.setMessage("Starting")
                    progressDialog!!.show()
                }

                override fun onFinish() {
                    progressDialog!!.hide()
                }
            })
        } catch (e: FFmpegCommandAlreadyRunningException) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View) {
        when {
            R.id.imgClose == v.id -> onBackPressed()
            R.id.imgDone == v.id -> saveImage()
            R.id.imgdraw2 == v.id -> setDrawingMode()
            R.id.imgText == v.id -> {
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
            }
            R.id.imgUndo == v.id -> mPhotoEditor!!.clearBrushAllViews()
            R.id.imgSticker == v.id -> mStickerBSFragment!!.show(
                supportFragmentManager, mStickerBSFragment!!.tag
            )
//            R.id.imgTrim == v.id -> {
//                masterVideoFile?.let { file ->
//                    val trimFragment = TrimFragment()
//                    trimFragment.setHelper(this)
//                    trimFragment.setFilePathFromSource(file, mediaPlayer?.duration!!.toLong())
//                    showBottomSheetDialogFragment(trimFragment)
//                }
//            }
//            R.id.imgPlayback == v.id -> {
//                masterVideoFile?.let { file ->
//
//                    SpeedFragment.newInstance().apply {
//                        setHelper(this@PreviewVideoActivity)
//                        setFilePathFromSource(file)
//                    }.show(supportFragmentManager, "OptiPlaybackSpeedDialogFragment")
//                }
//            }

            R.id.imgAddmusic == v.id -> {
                val timeInMillis = OptiUtils.getVideoDuration(applicationContext, masterVideoFile!!)
                val soundPickerFragment = SoundPickerFragment()
                soundPickerFragment.setFilePath(masterVideoFile!!)
                soundPickerFragment.setDuartion(timeInMillis)
                showBottomSheetDialogFragment(soundPickerFragment)
//                masterVideoFile?.let { file ->
//
//                    val timeInMillis = OptiUtils.getVideoDuration(applicationContext, file)
//                    /*val duration = OptiCommonMethods.convertDurationInSec(timeInMillis)
//                Log.v(tagName, "videoDuration: $duration")*/
//                   OptiAddMusicFragment.newInstance().apply {
//                        setHelper(this@PreviewVideoActivity)
//                        setFilePathFromSource(file)
//                        setDuration(timeInMillis)
//                    }.show(supportFragmentManager, "AddMusicFragment")
//                }
            }
            R.id.changesound == v.id -> {
                val filterFragment = FilterVideoFragment()
                filterFragment.setFilePathFromSource(masterVideoFile!!)
                filterFragment.setCallback(this)
                showBottomSheetDialogFragment(filterFragment)

//                startFilterActivity(videoPath!!)

//                masterVideoFile?.let { file ->
//                    val chnageSoundFragment = ChangeSoundFragment()
//                    chnageSoundFragment.setHelper(this)
//                    chnageSoundFragment.setFilePathFromSource(
//                        file,
//                        mediaPlayer?.duration!!.toLong()
//                    )
//                    showBottomSheetDialogFragment(chnageSoundFragment)
//                }
            }
        }
    }

    private fun startFilterActivity(uri: String) {
        val intent = EditVideoActivity.newIntent(this, uri)
        startActivity(intent)
    }


    private fun showBottomSheetDialogFragment(bottomSheetDialogFragment: BottomSheetDialogFragment) {
        val bundle = Bundle()
        bottomSheetDialogFragment.arguments = bundle
        bottomSheetDialogFragment.show(supportFragmentManager, bottomSheetDialogFragment.tag)
    }

    private fun setCanvasAspectRatio() {
        originalDisplayHeight = displayHeight
        originalDisplayWidth = displayWidth
        val displayDiamenion = Utils.getScaledDimension(
            DimensionData(
                DRAW_CANVASW, DRAW_CANVASH
            ),
            DimensionData(originalDisplayWidth, originalDisplayHeight)
        )
        newCanvasWidth = displayDiamenion.width
        newCanvasHeight = displayDiamenion.height
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
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + ""
                    + System.currentTimeMillis() + ".png"
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
                        this@PreviewVideoActivity.imagePath = imagePath
                        Log.d("imagePath>>", imagePath)
                        Log.d("imagePath2>>", Uri.fromFile(File(imagePath)).toString())
                        ivImage!!.source?.setImageURI(Uri.fromFile(File(imagePath)))
                        Toast.makeText(
                            this@PreviewVideoActivity,
                            "Saved successfully...",
                            Toast.LENGTH_SHORT
                        ).show()
                        applayWaterMark()
                    }

                    override fun onFailure(exception: Exception) {
                        Toast.makeText(
                            this@PreviewVideoActivity,
                            "Saving Failed...",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun applayWaterMark() {

//        imagePath = generatePath(Uri.fromFile(new File(imagePath)),PreviewVideoActivity.this);

//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        retriever.setDataSource(videoPath);
//        int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
//        int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
        /*if (width > height) {
            int tempWidth = width;
            width = height;
            height = tempWidth;
        }*/

//        Log.d(">>", "width>> " + width + "height>> " + height);
//        retriever.release();
        val output = File(
            Environment.getExternalStorageDirectory()
                .toString() + File.separator + ""
                    + System.currentTimeMillis() + ".mp4"
        )
        try {
            output.createNewFile()
            exeCmd!!.add("-y")
            exeCmd!!.add("-i")
            exeCmd!!.add(videoPath!!)
            //            exeCmd.add("-framerate 30000/1001 -loop 1");
            exeCmd!!.add("-i")
            exeCmd!!.add(imagePath!!)
            exeCmd!!.add("-filter_complex")
            //            exeCmd.add("-strict");
//            exeCmd.add("-2");
//            exeCmd.add("-map");
//            exeCmd.add("[1:v]scale=(iw+(iw/2)):(ih+(ih/2))[ovrl];[0:v][ovrl]overlay=x=(main_w-overlay_w)/2:y=(main_h-overlay_h)/2");
//            exeCmd.add("[1:v]scale=720:1280:1823[ovrl];[0:v][ovrl]overlay=x=0:y=0");
            exeCmd!!.add("[1:v]scale=$DRAW_CANVASW:$DRAW_CANVASH[ovrl];[0:v][ovrl]overlay=x=0:y=0")
            exeCmd!!.add("-c:v")
            exeCmd!!.add("libx264")
            exeCmd!!.add("-preset")
            exeCmd!!.add("ultrafast")
            exeCmd!!.add(output.absolutePath)
            newCommand = arrayOfNulls(exeCmd!!.size)
            for (j in exeCmd!!.indices) {
                newCommand[j] = exeCmd!![j]
            }
            for (k in newCommand.indices) {
                Log.d("CMD==>>", newCommand[k].toString() + "")
            }

//            newCommand = new String[]{"-i", videoPath, "-i", imagePath, "-preset", "ultrafast", "-filter_complex", "[1:v]scale=2*trunc(" + (width / 2) + "):2*trunc(" + (height/ 2) + ") [ovrl], [0:v][ovrl]overlay=0:0" , output.getAbsolutePath()};
            executeCommand(newCommand, output.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStickerClick(bitmap: Bitmap?) {
        mPhotoEditor!!.setBrushDrawingMode(false)
        imgDraw!!.setBackgroundColor(ContextCompat.getColor(this, R.color.black_trasp))
        mPhotoEditor!!.addImage(bitmap)
    }

    override fun onEditTextChangeListener(
        rootView: View?,
        text: String?,
        colorCode: Int,
        pos: Int
    ) {

        val textEditorDialogFragment = TextEditorDialogFragment.show(this, 0)
        textEditorDialogFragment.setOnTextEditorListener(object :
            TextEditorDialogFragment.TextEditor {
            override fun onDone(inputText: String?, colorCode: Int, position: Int) {
                val styleBuilder = TextStyleBuilder()
                styleBuilder.withTextColor(colorCode)
                val typeface: Typeface? =
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
        player.stop()
    }

    fun generatePath(uri: Uri, context: Context): String? {
        var filePath: String? = null
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
        if (isKitKat) {
            filePath = generateFromKitkat(uri, context)
        }
        if (filePath != null) {
            return filePath
        }
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(MediaStore.MediaColumns.DATA),
            null,
            null,
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return filePath ?: uri.path
    }

    @TargetApi(19)
    private fun generateFromKitkat(uri: Uri, context: Context): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val wholeID = DocumentsContract.getDocumentId(uri)
            val id = wholeID.split(":".toRegex()).toTypedArray()[1]
            val column = arrayOf(MediaStore.Video.Media.DATA)
            val sel = MediaStore.Video.Media._ID + "=?"
            val cursor = context.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                column, sel, arrayOf(id), null
            )
            val columnIndex = cursor!!.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return filePath
    }

    private val displayWidth: Int
        get() {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }
    private val displayHeight: Int
        get() {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
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

    override fun onOpacityChanged(opacity: Int) {
        mPhotoEditor!!.setOpacity(opacity)
    }

    override fun onBrushSizeChanged(brushSize: Int) {
        mPhotoEditor!!.brushSize = brushSize.toFloat()

    }

    companion object {
        private val TAG = PreviewVideoActivity::class.java.simpleName
        private const val CAMERA_REQUEST = 52
        private const val PICK_REQUEST = 53
    }

    override fun onProgress(progress: String) {
        Log.v(tagName, "onProgress()")
        showLoading(true)
    }

    override fun onSuccess(convertedFile: File, type: String) {
        showLoading(false)
        Log.v(tagName, "onSuccess()")
        Toast.makeText(applicationContext, "Video processing Success", Toast.LENGTH_LONG).show()
        onFileProcessed(convertedFile)
    }

    override fun onFailure(error: Exception) {
        Toast.makeText(applicationContext, "Video processing failed", Toast.LENGTH_LONG).show()
        Log.v(tagName, "onFailure ${error.localizedMessage}")
        showLoading(false)
    }

    override fun onNotAvailable(error: Exception) {
        Log.v(tagName, "onNotAvailable() ${error.localizedMessage}")
    }

    override fun onFinish() {
        Log.v(tagName, "onFinish()")
        showLoading(false)
        releasePlayer()

    }

    override fun onClick(v: View, position: Int) {
        mPosition = position
        ePlayerView.setGlFilter(
            FilterType.createGlFilter(
                FilterType.createFilterList()[mPosition],
                applicationContext
            )
        )
    }
}