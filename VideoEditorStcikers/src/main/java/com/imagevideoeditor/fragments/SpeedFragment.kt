package com.imagevideoeditor.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.R
import com.obs.marveleditor.OptiVideoEditor
import com.obs.marveleditor.fragments.OptiBaseCreatorDialogFragment
import com.obs.marveleditor.interfaces.OptiDialogueHelper
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import com.obs.marveleditor.interfaces.OptiPlaybackSpeedListener
import com.obs.marveleditor.utils.OptiConstant
import com.obs.marveleditor.utils.OptiUtils
import kotlinx.android.synthetic.main.fragment_playbackspeed.*
import kotlinx.android.synthetic.main.fragment_trim.iv_done
import java.io.File


class SpeedFragment : BottomSheetDialogFragment(), OptiDialogueHelper, OptiFFMpegCallback,
    OptiPlaybackSpeedListener {

    private var masterFile: File? = null
    private var isHavingAudio = true
    private var tagName: String = SpeedFragment::class.java.simpleName
    private var helper: OptiBaseCreatorDialogFragment.CallBacks? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var playbackSpeed: ArrayList<String> = ArrayList()
    private lateinit var optiPlaybackSpeedAdapter: com.imagevideoeditor.adaptors.OptiPlaybackSpeedAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playbackspeed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        linearLayoutManager = LinearLayoutManager(activity!!.applicationContext)
        iv_done.setOnClickListener {
            optiPlaybackSpeedAdapter.setPlayback()
        }

        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rvPlaybackSpeed.layoutManager = linearLayoutManager

        playbackSpeed.add(OptiConstant.SPEED_0_25)
        playbackSpeed.add(OptiConstant.SPEED_0_5)
        playbackSpeed.add(OptiConstant.SPEED_0_75)
        playbackSpeed.add(OptiConstant.SPEED_1_0)
        playbackSpeed.add(OptiConstant.SPEED_1_25)
        playbackSpeed.add(OptiConstant.SPEED_1_5)

        optiPlaybackSpeedAdapter = com.imagevideoeditor.adaptors.OptiPlaybackSpeedAdapter(
            playbackSpeed,
            activity!!.applicationContext,
            this
        )
        rvPlaybackSpeed.adapter = optiPlaybackSpeedAdapter
        optiPlaybackSpeedAdapter.notifyDataSetChanged()
    }


    companion object {
        fun newInstance() = SpeedFragment()
    }

    override fun setHelper(helper: OptiBaseCreatorDialogFragment.CallBacks) {
        this.helper = helper
    }

    override fun setMode(mode: Int) {
    }

    override fun setFilePathFromSource(file: File) {
        masterFile = file
        isHavingAudio = OptiUtils.isVideoHaveAudioTrack(file.absolutePath)
    }

    override fun setDuration(duration: Long) {

    }

    override fun onProgress(progress: String) {

    }

    override fun onSuccess(convertedFile: File, type: String) {
        Log.d(tagName, "onSuccess()")
        helper?.showLoading(false)
        helper?.onFileProcessed(convertedFile)
    }

    override fun onFailure(error: Exception) {
        Log.d(tagName, "onFailure() " + error.localizedMessage)
        Toast.makeText(requireContext(), "Video processing failed", Toast.LENGTH_LONG).show()
        helper?.showLoading(false)
    }

    override fun onNotAvailable(error: Exception) {
        Log.d(tagName, "onNotAvailable() " + error.message)
        helper?.showLoading(false)
    }

    override fun onFinish() {
        Log.d(tagName, "onFinish()")
        helper?.showLoading(false)
    }

    override fun processVideo(playbackSpeed: String, tempo: String) {
        if (playbackSpeed != "0.0") {
            //output file is generated and send to video processing
            val outputFile = OptiUtils.createVideoFile(context!!)
            Log.v(tagName, "outputFile: ${outputFile.absolutePath}")

            OptiVideoEditor.with(context!!)
                .setType(OptiConstant.VIDEO_PLAYBACK_SPEED)
                .setFile(masterFile!!)
                .setOutputPath(outputFile.absolutePath)
                .setIsHavingAudio(isHavingAudio)
                .setSpeedTempo(playbackSpeed, tempo)
                .setCallback(this)
                .main()

            helper?.showLoading(true)
            dismiss()
        } else {
            OptiUtils.showGlideToast(activity!!, getString(R.string.error_select_speed))
        }
    }
}