package com.obs.marveleditor

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.obs.marveleditor.videoTrimmer.OptiHgLVideoTrimmer
import com.obs.marveleditor.videoTrimmer.interfaces.OptiOnHgLVideoListener
import com.obs.marveleditor.videoTrimmer.interfaces.OptiOnTrimVideoListener

class OptiTrimmerActivity : AppCompatActivity(), OptiOnTrimVideoListener, OptiOnHgLVideoListener {
    private var mVideoTrimmer: OptiHgLVideoTrimmer? = null
    private var mProgressDialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.opti_activity_trimmer)
        val extraIntent = intent
        var path: String? = ""
        var maxDuration = 10
        if (extraIntent != null) {
            path = extraIntent.getStringExtra("VideoPath")
            maxDuration = extraIntent.getIntExtra("VideoDuration", 10)
        }

        //setting progressbar
        mProgressDialog = ProgressDialog(this)
        mProgressDialog!!.setCancelable(false)
        mProgressDialog!!.setMessage(getString(R.string.trimming_progress))
        mVideoTrimmer = findViewById(R.id.timeLine)
        if (mVideoTrimmer != null) {
            //get total duration of video file
            Log.e("tg", "maxDuration = $maxDuration")
            mVideoTrimmer!!.setMaxDuration(maxDuration)
            mVideoTrimmer!!.setOnTrimVideoListener(this)
            mVideoTrimmer!!.setOnHgLVideoListener(this)
            mVideoTrimmer!!.setVideoURI(Uri.parse(path))
            mVideoTrimmer!!.setVideoInformationVisibility(true)
        }
    }

    override fun onTrimStarted(startPosition: Int, endPosition: Int) {
        //mProgressDialog.show();
        //selected startPosition & endPosition is passed
        val intent = Intent()
        intent.putExtra("startPosition", startPosition)
        intent.putExtra("endPosition", endPosition)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun getResult(contentUri: Uri?) {
        mProgressDialog!!.cancel()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("tg", "resultCode = $resultCode data $data")
    }

    override fun cancelAction() {
        mProgressDialog!!.cancel()
        mVideoTrimmer!!.destroy()
        finish()
    }

    override fun onError(message: String?) {
        TODO("Not yet implemented")
    }
    

    override fun onVideoPrepared() {
        runOnUiThread {}
    }
}