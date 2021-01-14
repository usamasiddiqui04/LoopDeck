package com.imagevideoeditor.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.R
import com.obs.marveleditor.OptiVideoEditor
import com.obs.marveleditor.fragments.OptiBaseCreatorDialogFragment
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import com.obs.marveleditor.utils.OptiConstant
import com.obs.marveleditor.utils.OptiUtils
import kotlinx.android.synthetic.main.fragment_change_sound.*
import kotlinx.android.synthetic.main.fragment_trim.*
import java.io.File


class ChangeSoundFragment : BottomSheetDialogFragment(), OptiFFMpegCallback {


    private var tagName: String = ChangeSoundFragment::class.java.simpleName
    private var helper: OptiBaseCreatorDialogFragment.CallBacks? = null
    private lateinit var rootView: View
    private var videoFile: File? = null
    private var duration: Long? = null
    private var chanegbutton: Button? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_change_sound, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnChangeSound.setOnClickListener {
            val outputFile = OptiUtils.createVideoFile(context!!)

            OptiVideoEditor.with(requireContext())
                .setType(OptiConstant.CHANGE_VIDEO_SOUND_FREQUENCY)
                .setFile(videoFile!!)
                .setOutputPath(outputFile.path)
                .setCallback(this)
                .main()
            helper?.showLoading(true)
            dismiss()
        }
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