package com.imagevideoeditor.filter

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_preview_filter.*
import java.io.File
import com.daasuu.epf.EPlayerView
import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.Rotation
import com.daasuu.mp4compose.composer.Mp4Composer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.R
import com.imagevideoeditor.filter.adapter.AddFilterAdapter
import com.imagevideoeditor.filter.interfaces.AddFilterListener
import com.imagevideoeditor.filter.interfaces.FilterVideoCallBack
import com.imagevideoeditor.filter.share.ShareActivity
import com.imagevideoeditor.filter.utils.FilterSave
import com.imagevideoeditor.filter.utils.FilterType
import com.obs.marveleditor.OptiVideoEditor
import com.obs.marveleditor.interfaces.OptiFFMpegCallback

class FilterVideoFragment : BottomSheetDialogFragment(), AddFilterListener {

    private lateinit var mAppName: String
    private lateinit var mAppPath: File
    private var filtervideocallback: FilterVideoCallBack? = null
    lateinit var player: SimpleExoPlayer
    lateinit var ePlayerView: EPlayerView

    private lateinit var filepath: String
    private lateinit var filename: String
    private lateinit var filterFilepath: String

    private var mPosition: Int = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_preview_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAppName = getString(R.string.app_name)
        mAppPath = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES),
            mAppName
        )

//        filepath = arguments?.getString(ARG_KEY_URI) ?: ""
        filename = filepath.substring(filepath.lastIndexOf("/") + 1)
        filterFilepath = "$mAppPath/MP4_$filename"

        setHasOptionsMenu(true)

        val adapter = AddFilterAdapter(this, filepath)

        recyclerView.apply {
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            onFlingListener = null
        }

        adapter.notifyDataSetChanged()

        saveVideoWithFilter()
    }

    fun setFilePathFromSource(file: File) {
        filepath = file.toString()
    }
//
//    private fun setToolbar() {
//        val supportToolbar = toolbar as Toolbar
//        (activity as AppCompatActivity).setSupportActionBar(supportToolbar)
//        supportToolbar.apply {
//            navigationIcon = ContextCompat.getDrawable(context, R.drawable.ic_arrow_back)
//            setNavigationOnClickListener {
//                activity?.onBackPressed()
//            }
//        }
//        activity?.title = ""
//    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_next, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.menu_next -> {
//                saveVideoWithFilter()
//                true
//            }
//            else -> false
//        }
//    }

    private fun saveVideoWithFilter() {

        if (!mAppPath.exists()) {
            mAppPath.mkdirs()
        }
//
        val dialog = context?.let {
            MaterialDialog.Builder(it)
                .content(R.string.msg_dialog)
                .progress(true, 0)
                .cancelable(false)
                .show()
        }

        if (mPosition != 0) {
            Mp4Composer(filepath, filterFilepath)
                .rotation(Rotation.NORMAL)
                .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                .filter(
                    FilterSave.createGlFilter(
                        FilterSave.createFilterList()[mPosition],
                        context
                    )
                )
                .listener(object : Mp4Composer.Listener {
                    override fun onProgress(progress: Double) {
                        Log.d(mAppName, "onProgress Filter = " + progress * 100)
                    }

                    override fun onCompleted() {
                        Log.d(mAppName, "onCompleted() Filter : $filterFilepath")
                        dialog?.dismiss()
//                        activity?.finish()
//                        val intent = ShareActivity.newIntent(context, filterFilepath)
//                        startActivity(intent)
                    }

                    override fun onCanceled() {
                        dialog?.dismiss()
                        Log.d(mAppName, "onCanceled")
                    }

                    override fun onFailed(exception: Exception) {
                        dialog?.dismiss()
                        Log.e(mAppName, "onFailed() Filter", exception)
                    }
                })
                .start()
        } else {
            dialog?.dismiss()
            Toast.makeText(context, "Not use filter", Toast.LENGTH_SHORT).show()
        }
    }

    //
    private fun setUpSimpleExoPlayer() {

        val dataSourceFactory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, mAppName)
        )
        val videoSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.fromFile(File(filepath)))

        player = ExoPlayerFactory.newSimpleInstance(context).apply {
            prepare(videoSource)
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    fun setCallback(callback: FilterVideoCallBack) {
        filtervideocallback = callback

    }

    //
    private fun setUpGlPlayerView() {
        ePlayerView = EPlayerView(context).apply {
            setSimpleExoPlayer(player)
        }
//        filterView.addView(ePlayerView)
        ePlayerView.onResume()
    }

    override fun onResume() {
        super.onResume()
        setUpSimpleExoPlayer()
        setUpGlPlayerView()
    }

    //
    private fun releasePlayer() {
        ePlayerView.onPause()
        player.stop()
        player.release()
    }

    //
    override fun onClick(v: View, position: Int) {
        mPosition = position
        ePlayerView.setGlFilter(
            FilterType.createGlFilter(
                FilterType.createFilterList()[mPosition],
                context
            )
        )

        filtervideocallback!!.SaveFilterVideoFilePath(filterFilepath)
    }
}
