package com.imagevideoeditor.filter

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.R
import com.imagevideoeditor.filter.adapter.AddFilterAdapter
import com.imagevideoeditor.filter.interfaces.AddFilterListener
import kotlinx.android.synthetic.main.fragment_preview_filter.*
import java.io.File

class FilterVideoFragment : BottomSheetDialogFragment() {

    private lateinit var mAppName: String
    private lateinit var mAppPath: File
    private var filterListener: AddFilterListener? = null

    private lateinit var filepath: String
    private lateinit var filename: String
    private lateinit var filterFilepath: String


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_preview_filter, container, false)
    }

    private val onItemClickListener: (View, Int) -> Unit =
        { itemView, position ->
            filterListener!!.onClick(itemView, position)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAppName = getString(R.string.app_name)
        mAppPath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            mAppName
        )
        filename = filepath.substring(filepath.lastIndexOf("/") + 1)
        filterFilepath = "$mAppPath/MP4_$filename"

        val adapter = AddFilterAdapter(filepath, onItemClickListener)

        recyclerView.apply {
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            onFlingListener = null
        }

        adapter.notifyDataSetChanged()
    }

    fun setFilePathFromSource(file: File) {
        filepath = file.toString()
    }

//    private fun saveVideoWithFilter() {
//
//        if (!mAppPath.exists()) {
//            mAppPath.mkdirs()
//        }
////
//        val dialog = context?.let {
//            MaterialDialog.Builder(it)
//                .content(R.string.msg_dialog)
//                .progress(true, 0)
//                .cancelable(false)
//                .show()
//        }
//
//        if (mPosition != 0) {
//            Mp4Composer(filepath, filterFilepath)
//                .rotation(Rotation.NORMAL)
//                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
//                .filter(
//                    FilterSave.createGlFilter(
//                        FilterSave.createFilterList()[mPosition],
//                        context
//                    )
//                )
//                .listener(object : Mp4Composer.Listener {
//                    override fun onProgress(progress: Double) {
//                        Log.d(mAppName, "onProgress Filter = " + progress * 100)
//                    }
//
//                    override fun onCompleted() {
//                        Log.d(mAppName, "onCompleted() Filter : $filterFilepath")
//                        dialog?.dismiss()
////                        activity?.finish()
////                        val intent = ShareActivity.newIntent(context, filterFilepath)
////                        startActivity(intent)
//                    }
//
//                    override fun onCanceled() {
//                        dialog?.dismiss()
//                        Log.d(mAppName, "onCanceled")
//                    }
//
//                    override fun onFailed(exception: Exception) {
//                        dialog?.dismiss()
//                        Log.e(mAppName, "onFailed() Filter", exception)
//                    }
//                })
//                .start()
//        } else {
//            dialog?.dismiss()
//            Toast.makeText(context, "Not use filter", Toast.LENGTH_SHORT).show()
//        }
//    }

    //

    fun setCallback(callback: AddFilterListener) {
        filterListener = callback

    }

}
