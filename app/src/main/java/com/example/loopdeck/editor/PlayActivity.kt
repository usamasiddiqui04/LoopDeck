package com.example.loopdeck.editor

import android.app.ProgressDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.ui.collection.publish.PublishViewModel
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import com.example.loopdeck.utils.extensions.toast
import com.obs.marveleditor.OptiVideoEditor
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import com.obs.marveleditor.utils.OptiConstant
import com.obs.marveleditor.utils.OptiUtils
import kotlinx.android.synthetic.main.activity_play.*
import kotlinx.android.synthetic.main.fragment_googlrdrive.toolbar
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PlayActivity : AppCompatActivity(), OptiFFMpegCallback {

    var videoIncrementer = 0
    var duration = 0
    var videoView: VideoView? = null
    private var mediaList = ArrayList<MediaData>()
    var progressDialog: ProgressDialog? = null
    private var tagName: String = PlayActivity::class.java.simpleName
    private var isHavingAudio = true
    val listOfImages = mutableListOf<File>()
    val listOfVidoes = mutableListOf<File>()
    private lateinit var viewModel: PublishViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        viewModel = activityViewModelProvider()
        progressDialog = ProgressDialog(this)
        val bundle = intent.extras
        mediaList =
            bundle!!.getParcelableArrayList<MediaData>("videoFileList") as ArrayList<MediaData>
        videoView = findViewById<View>(R.id.playVideo) as VideoView

        isHavingAudio = OptiUtils.isVideoHaveAudioTrack(mediaList[0].filePath)
        Log.d(tagName, "isHavingAudio $isHavingAudio")

        mediaList.forEach {
            if (it.mediaType == "image") {
                listOfImages.add(File(it.filePath))
            }
        }
        setMediaController()




        publish.setOnClickListener {
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

    }

    private fun publishData() {
        if (mediaList.size > 1) {

            val fileList = mutableListOf<File>()
            mediaList.forEach {
                if (it.filePath.contains("mp4"))
                    fileList.add(File(it.filePath))
            }

            val outputFile = applicationContext.let { it1 -> OptiUtils.createVideoFile(it1) }

            outputFile.let {
                OptiVideoEditor.with(applicationContext)
                    .setType(OptiConstant.MERGE_VIDEO)
                    .setMutlipleFiles(fileList)
                    .setOutputPath(it.path)
                    .setCallback(this)
                    .main()
            }
        } else if (mediaList.isEmpty()) {
            toast("Please select video files to merge and play")
        } else {
            viewModel.publishedFiles(File(mediaList[0].filePath))
            toast("Saved to publish")
        }

    }

    private fun setMediaController() {
        videoView!!.setVideoURI(Uri.parse(mediaList[videoIncrementer].filePath))
        videoView!!.requestFocus()

        videoView!!.setOnPreparedListener {
            if (mediaList[videoIncrementer].filePath.contains(".mp4"))
                videoView!!.start()

            val time = getDate(videoView!!.duration.toLong(), "mm:ss")
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
                videoIncrementer = if (++videoIncrementer < mediaList.size) videoIncrementer else 0
                mp.setDataSource(mediaList[videoIncrementer].filePath)
                mp.prepare()
                mp.start()

            }
        }
    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }


    override fun onProgress(progress: String) {
        progressDialog!!.setMessage(progress)
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()
    }

    override fun onSuccess(convertedFile: File, type: String) {
        toast("Saved to publish")
        viewModel.publishedFiles(convertedFile)
        progressDialog!!.dismiss()
    }

    override fun onFailure(error: Exception) {
        progressDialog!!.dismiss()
        Log.d(tagName, "onFailure " + error.message)

        toast(error.toString())
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