package com.xorbics.loopdeck.editor.fragments

import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.guilhe.views.SeekBarRangedView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.R
import com.obs.marveleditor.OptiVideoEditor
import com.obs.marveleditor.fragments.OptiBaseCreatorDialogFragment
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import com.obs.marveleditor.utils.OptiConstant
import com.obs.marveleditor.utils.OptiUtils
import com.obs.marveleditor.utils.VideoUtils
import kotlinx.android.synthetic.main.fragment_trim.*
import java.io.File


class TrimFragment : BottomSheetDialogFragment(), OptiFFMpegCallback {

    private var tagName: String = TrimFragment::class.java.simpleName
    private var helper: OptiBaseCreatorDialogFragment.CallBacks? = null
    private lateinit var rootView: View
    private var videoFile: File? = null
    private var duration: Long? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_trim, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        VideoTrim?.minValue = 0f
        VideoTrim?.maxValue = duration?.toFloat()!!
        StartTime?.text = VideoUtils.secToTime(0)
        EndTime?.text = VideoUtils.secToTime(duration!!)



        iv_done.setOnClickListener {
            //output file is generated and send to video processing
            val outputFile = OptiUtils.createVideoFile(context!!)

            OptiVideoEditor.with(requireContext())
                .setType(OptiConstant.VIDEO_TRIM)
                .setVideoFile(videoFile!!)
                .setOutputPath(outputFile.path)
                .setStartTime(StartTime?.text.toString())
                .setEndTime(EndTime?.text.toString())
                .setCallback(this)
                .main()
            helper?.showLoading(true)
            dismiss()
        }


        VideoTrim?.setOnSeekBarRangedChangeListener(object :
            SeekBarRangedView.OnSeekBarRangedChangeListener {
            override fun onChanged(view: SeekBarRangedView?, minValue: Float, maxValue: Float) {
                //exoPlayer?.seekTo(minValue.toLong())
            }

            override fun onChanging(view: SeekBarRangedView?, minValue: Float, maxValue: Float) {
                StartTime?.text = VideoUtils.secToTime(minValue.toLong())
                EndTime?.text = VideoUtils.secToTime(maxValue.toLong())
            }
        })
    }

    fun setFilePathFromSource(file: File, duration: Long) {
        videoFile = file
        this.duration = duration
    }

    fun setHelper(helper: OptiBaseCreatorDialogFragment.CallBacks) {
        this.helper = helper
    }

    override fun onProgress(progress: String) {
        helper?.showLoading(true)
    }

    override fun onSuccess(convertedFile: File, type: String) {
        helper?.showLoading(false)
        helper?.onFileProcessed(convertedFile)
    }

    override fun onFailure(error: Exception) {
        helper?.showLoading(false)
    }

    override fun onNotAvailable(error: Exception) {
        Log.d(tagName, "onNotAvailable() " + error.message)
        Log.v(tagName, "Exception: ${error.localizedMessage}")
    }

    override fun onFinish() {
        Log.d(tagName, "onFinish()")
        helper?.showLoading(false)
    }

}