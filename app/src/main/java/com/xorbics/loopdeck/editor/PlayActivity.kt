package com.xorbics.loopdeck.editor

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.xorbics.loopdeck.R
import com.xorbics.loopdeck.data.MediaData
import com.xorbics.loopdeck.data.PublishData
import com.xorbics.loopdeck.ui.collection.publish.PublishViewModel
import com.xorbics.loopdeck.utils.extensions.activityViewModelProvider
import com.xorbics.loopdeck.utils.extensions.toast
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException
import com.obs.marveleditor.OptiVideoEditor
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import com.obs.marveleditor.utils.OptiConstant
import com.obs.marveleditor.utils.OptiUtils
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.fragment_googlrdrive.toolbar
import java.io.File
import java.util.*


class PlayActivity : AppCompatActivity(), OptiFFMpegCallback {

    var videoIncrementer = 0
    var duration = 0
    private var fFmpeg: FFmpeg? = null
    private var nextAction: Int = 1
    var videoView: VideoView? = null
    private var mediaList = ArrayList<MediaData>()
    private var mediaList2 = ArrayList<PublishData>()
    var progressDialog: ProgressDialog? = null
    private var tagName: String = PlayActivity::class.java.simpleName
    private var isHavingAudio = true
    var filePath: String? = null
    val listOfImages = mutableListOf<File>()
    val listOfVidoes = mutableListOf<File>()
    private lateinit var viewModel: PublishViewModel
    private var silentsound: String? = null
    private var isPublishedVideo = false

    var downloadsDirectoryPath: String? = null
    var count: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        viewModel = activityViewModelProvider()
        progressDialog = ProgressDialog(this)
        fFmpeg = FFmpeg.getInstance(this)
        loadffmpeg()

        downloadsDirectoryPath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath


        val bundle = intent.extras
        isPublishedVideo = bundle!!.getBoolean("isPublishedVideo")

        if (isPublishedVideo) {
            publish.visibility = View.GONE
            filePath =
                bundle.getString("filePath")
            listOfVidoes.addAll(listOf(File(filePath!!)))
        } else {
            publish.visibility = View.VISIBLE
            mediaList =
                bundle.getParcelableArrayList<MediaData>("videoFileList") as ArrayList<MediaData>
            mediaList.forEach {
                if (it.mediaType == "image") {
                    listOfImages.add(File(it.filePath))
                }
                if (it.mediaType == "video") {
                    listOfVidoes.add(File(it.filePath))
                }
            }
        }


        videoView = findViewById<View>(R.id.playVideo) as VideoView
        Log.d(tagName, "isHavingAudio $isHavingAudio")


        publish.setOnClickListener {
            nextAction = 2
            publishData()
        }

        btnplay.setOnClickListener {

            btnplay.visibility = View.GONE
            btnpause.visibility = View.VISIBLE
            btnrestart.visibility = View.GONE
            videoView!!.start()

        }
        btnrestart.setOnClickListener {
            videoIncrementer = 0
            btnplay.visibility = View.GONE
            btnpause.visibility = View.VISIBLE
            btnrestart.visibility = View.GONE
            setMediaController()
        }

        btnpause.setOnClickListener {
            btnplay.visibility = View.VISIBLE
            btnpause.visibility = View.GONE
            btnrestart.visibility = View.GONE
            videoView!!.pause()
        }

        toolbar.setNavigationIcon(R.drawable.ic_back_black)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        if (listOfImages.isNotEmpty()) {
            applySoundOnImages(listOfImages[count].path)
        } else {
            setMediaController()
        }
    }

    private fun loadffmpeg() {
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

    private fun applySoundOnImages(path: String) {
        viewModel.saveSilentFileToMobileDevice(this)
        val file = File(downloadsDirectoryPath, "silent.mp3")
        nextAction = 1
        val outputFile = applicationContext.let { it1 -> OptiUtils.createVideoFile(it1) }
        OptiVideoEditor.with(this)
            .setType(OptiConstant.IMAGE_AUDIO_MERGE)
            .setImageFile(File(path))
            .setAudioFile(file)
            .setOutputPath(outputFile.path)
            .setCallback(this)
            .main()
    }

    private fun publishData() {
        if (listOfVidoes.size > 1) {

            val fileList = mutableListOf<File>()
            listOfVidoes.forEach {
                fileList.add(File(it.path))
            }
            val outputFile = applicationContext.let { it1 -> OptiUtils.createVideoFile(it1) }

            outputFile.let {
                OptiVideoEditor.with(this)
                    .setType(OptiConstant.MERGE_VIDEO)
                    .setMutlipleFiles(fileList)
                    .setOutputPath(it.path)
                    .setCallback(this)
                    .main()
            }
        } else if (mediaList.isEmpty()) {
            toast("Please select video files to merge and play")
        } else {
            viewModel.publishedFiles(File(listOfVidoes[0].path))
            toast("Saved to publish")
        }

    }

    private fun setMediaController() {
        if (listOfVidoes.isNotEmpty()) {
            videoView!!.setVideoURI(Uri.parse(listOfVidoes[videoIncrementer].path))
            videoView!!.requestFocus()

            videoView!!.setOnPreparedListener {

                videoView!!.start()

                val time = viewModel.getDate(videoView!!.duration.toLong(), "mm:ss")
                videoTime.text = time.toString()
            }

            videoView!!.setOnCompletionListener { mp ->

                if (videoIncrementer == mediaList.size - 1) {
                    mp!!.stop()
                    btnplay.visibility = View.GONE
                    btnpause.visibility = View.GONE
                    btnrestart.visibility = View.VISIBLE

                } else {
                    mp!!.stop()
                    mp.reset()
                    videoIncrementer =
                        if (++videoIncrementer < listOfVidoes.size) videoIncrementer else 0
                    mp.setDataSource(listOfVidoes[videoIncrementer].path)
                    mp.prepare()
                    mp.start()

                }
            }
        } else {
            videoView?.visibility = View.GONE
        }
    }


    override fun onProgress(progress: String) {
        progressDialog!!.setMessage(progress)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()
    }

    override fun onSuccess(convertedFile: File, type: String) {
        if (nextAction == 2) {
            toast("Saved to publish")
            viewModel.publishedFiles(convertedFile)
            progressDialog!!.dismiss()
        }

        if (nextAction == 1) {
            listOfVidoes.add(convertedFile)
            toast(listOfVidoes.size.toString())
            if (count == listOfImages.size - 1) {  // size = 2  ,   count = 1
                progressDialog!!.dismiss()
                setMediaController()
                toast("All images converted to video successfully")
            } else {
                count += 1
                applySoundOnImages(listOfImages[count].path)
            }
        }
    }

    override fun onFailure(error: Exception) {
        progressDialog!!.dismiss()
        Log.d(tagName, "onFailure " + error.message)

        toast("Failure : $error")
    }

    override fun onNotAvailable(error: Exception) {
        Log.d(tagName, "onNotAvailable() " + error.message)
        Log.v(tagName, "Exception: ${error.localizedMessage}")

        progressDialog?.dismiss()
    }

    override fun onFinish() {
        Log.d(tagName, "onFinish()")
        progressDialog?.dismiss()
    }


}