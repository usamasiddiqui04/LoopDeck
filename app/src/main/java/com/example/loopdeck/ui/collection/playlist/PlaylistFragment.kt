package com.example.loopdeck.ui.collection.playlist

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.DragData
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaType
import com.example.loopdeck.editor.PreviewPhotoActivity
import com.example.loopdeck.editor.PreviewVideoActivity
import com.example.loopdeck.gallery.model.GalleryData
import com.example.loopdeck.gallery.view.PickerActivity
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.utils.callbacks.ItemMoveCallback
import com.example.loopdeck.utils.extensions.toast
import com.obs.marveleditor.OptiVideoEditor
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import com.obs.marveleditor.utils.OptiConstant
import com.obs.marveleditor.utils.OptiUtils
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*
import kotlinx.android.synthetic.main.item_recent_folder_list.view.selectitem
import kotlinx.android.synthetic.main.item_recent_list_images.view.*
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*
import java.io.File
import java.util.*


class PlaylistFragment : Fragment(), OptiFFMpegCallback {

    private var Selectlist = ArrayList<MediaData>()
    private var tagName: String = PlaylistFragment::class.java.simpleName
    private var viewholder: RecyclerView.ViewHolder? = null
    var filepath: String? = null
    private var selectedList = ArrayList<MediaData>()
    var mediaData: MediaData? = null
    var multiSelection: Boolean = false
    var progressDialog: ProgressDialog? = null
    var sharedpreferences: SharedPreferences? = null

    private val playlistName by lazy {
        arguments?.getString(KEY_NAME)
    }

    companion object {
        private const val KEY_NAME = "key_name"
        fun newInstance(playlistName: String) = PlaylistFragment().apply {
            val args = Bundle()
            args.putString(KEY_NAME, playlistName)
            arguments = args
        }

        private const val REQUEST_RESULT_CODE = 0
    }

    private lateinit var viewModel: CollectionViewModel

    private val mediaAdapter by lazy {
        MediaAdaptor(
            mutableListOf(),
            onItemClickListener,
            onItemLongClickListener,
            viewModel::onSequenceChanged,
        )
    }


    private val onItemLongClickListener: (View, RecyclerView.ViewHolder, MutableList<MediaData>, MediaData) -> Unit =
        { itemView, viewHolder, list, mediadata ->

            multiSelection = true
            toggleSelection(viewHolder, mediadata, list)

        }

    private fun toggleSelection(
        viewHolder: RecyclerView.ViewHolder,
        mediadata: MediaData,
        list: MutableList<MediaData>
    ) {
        mediaData = list.get(viewHolder.adapterPosition)
        when (mediadata.mediaType) {
            MediaType.IMAGE -> {

                if (viewHolder.itemView.selectitem.visibility == View.GONE) {
                    viewHolder.itemView.selectitem.visibility = View.VISIBLE
                    viewHolder.itemView.cardview.alpha = 0.5f
                    selectedList.add(mediaData!!)

                } else {
                    viewHolder.itemView.selectitem.visibility = View.GONE
                    viewHolder.itemView.cardview.alpha = 1f
                    selectedList.remove(mediaData!!)
                }
            }
            MediaType.VIDEO -> {
                if (viewHolder.itemView.selectitem.visibility == View.GONE) {
                    viewHolder.itemView.selectitem.visibility = View.VISIBLE
                    viewHolder.itemView.cardvideo.alpha = 0.5f
                    selectedList.add(mediaData!!)
                } else {
                    viewHolder.itemView.selectitem.visibility = View.GONE
                    viewHolder.itemView.cardvideo.alpha = 1f
                    selectedList.remove(mediaData!!)
                }
            }
            else -> {
                if (viewHolder.itemView.selectitem.visibility == View.GONE) {
                    viewHolder.itemView.selectitem.visibility = View.VISIBLE
                    viewHolder.itemView.cardfolder.alpha = 0.5f
                    selectedList.add(mediaData!!)
                } else {
                    viewHolder.itemView.selectitem.visibility = View.GONE
                    viewHolder.itemView.cardfolder.alpha = 1f
                    selectedList.remove(mediaData!!)
                }
            }
        }
    }

    private val onItemClickListener: (View, RecyclerView.ViewHolder, MutableList<MediaData>, MediaData) -> Unit =
        { itemView, viewHolder, list, mediadata ->

            multiSelection = !selectedList.isEmpty()
            if (!multiSelection) {
                when (mediadata.mediaType) {
                    MediaType.IMAGE -> {
                        val intent = Intent(requireContext(), PreviewPhotoActivity::class.java)
                        intent.putExtra("mediaData", mediadata)
                        startActivity(intent)
                    }
                    MediaType.VIDEO -> {
                        val intent = Intent(requireContext(), PreviewVideoActivity::class.java)
                        intent.putExtra("mediaData", mediadata)
                        startActivity(intent)

                    }
                    else -> {
                        requireActivity().supportFragmentManager.beginTransaction()
                            .replace(R.id.container, PlaylistFragment.newInstance(mediadata.name))
                            .addToBackStack(null)
                            .commit()

                    }
                }
            } else {

                toggleSelection(viewHolder, mediadata, list)
            }

        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CollectionViewModel::class.java)
        initViews()
        initObservers()
    }


    private fun initViews() {
        playlistname.text = playlistName
        var touchHelper: ItemTouchHelper? = null
        recyclerview?.adapter = mediaAdapter
        recyclerview?.layoutManager = GridLayoutManager(requireContext(), 2)
        val callback = ItemMoveCallback(mediaAdapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerview)
        progressDialog = ProgressDialog(requireContext())



        btnGallery.setOnClickListener {

            val i = Intent(activity, PickerActivity::class.java)
            i.putExtra("IMAGES_LIMIT", 100)
            i.putExtra("VIDEOS_LIMIT", 100)
            i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
            i.putExtra("playlistName", playlistName)
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }

        btnDelete.setOnClickListener {
            deleteMediaFiles()
        }

        btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        btnplay.setOnClickListener {

            if (selectedList.size > 0) {

                val fileList = mutableListOf<File>()
                selectedList.forEach {
                    fileList.add(File(it.filePath))
                }

                val outputFile = context?.let { it1 -> OptiUtils.createVideoFile(it1) }

                outputFile?.let {
                    toast(outputFile.path.toString())
                    OptiVideoEditor.with(context!!)
                        .setType(OptiConstant.MERGE_VIDEO)
                        .setMutlipleFiles(fileList)
                        .setOutputPath(it.path)
                        .setCallback(this)
                        .main()
                }
            } else {
                toast("Please select video files to merge and play")
            }

        }

        btnDublicate.setOnClickListener {

            selectedList.forEach {
                viewModel.dublicateMediafiles(it, playlistName)
            }
        }

        initContainer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUEST_RESULT_CODE && data != null) {
            val mediaList = data.getParcelableArrayListExtra<GalleryData>("MEDIA")
            viewModel.addMediaFiles(mediaList!!, playlistName)

        }
    }


    private fun initObservers() {

        playlistName?.let {
            viewModel.getPlaylistMedia(it).observe(viewLifecycleOwner, Observer { list ->
                mediaAdapter.submitList(list)
            })
        }
    }


    private fun initContainer() {
        btnDelete.setOnDragListener { view, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_ENTERED -> btnDelete.setBackgroundColor(Color.GREEN)
                DragEvent.ACTION_DRAG_EXITED -> btnDelete.setBackgroundColor(Color.RED)
                DragEvent.ACTION_DRAG_ENDED -> btnDelete.setBackgroundColor(Color.WHITE)
                DragEvent.ACTION_DROP -> {
                    //  final float dropX = dragEvent.getX();
                    //  final float dropY = dragEvent.getY();
                    val state = dragEvent.localState as DragData
                    deleteFile(state.item)
                }
                else -> {
                }
            }
            true
        }
    }

    private fun deleteFile(mediaData: MediaData) {

        viewModel.delete(mediaData)
        Toast.makeText(
            requireContext(),
            " Deleted successfully ${mediaData.name}",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onProgress(progress: String) {
        progressDialog!!.setMessage("Converting please wait")
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()
    }

    override fun onSuccess(convertedFile: File, type: String) {
        toast("Success")
        toast(convertedFile.toString())
        viewModel.editedImageFiles(convertedFile, playlistName)
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
    }

    override fun onFinish() {
        Log.d(tagName, "onFinish()")
    }

    private fun deleteMediaFiles() {
        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                for (list in selectedList) {
                    viewModel.delete(list)
                }
                selectedList.clear()
            }
        }, 1000)

    }


}
