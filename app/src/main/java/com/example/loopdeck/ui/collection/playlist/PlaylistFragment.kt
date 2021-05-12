package com.example.loopdeck.ui.collection.playlist

import android.app.ProgressDialog
import android.content.Intent
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
import com.example.loopdeck.DragData
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.editor.PlayActivity
import com.example.loopdeck.gallery.model.GalleryData
import com.example.loopdeck.gallery.view.PickerActivity
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.utils.callbacks.ItemMoveCallback
import com.example.loopdeck.utils.extensions.toast
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.fragment_playlist.btnDelete
import kotlinx.android.synthetic.main.fragment_playlist.btnplay
import kotlinx.android.synthetic.main.fragment_playlist.recyclerview
import java.io.File


class PlaylistFragment : Fragment(), OptiFFMpegCallback, MediaAdaptor.OnItemClick {

    private var tagName: String = PlaylistFragment::class.java.simpleName
    private var selectedList = ArrayList<MediaData>()
    var mediaData: MediaData? = null
    var multiSelection: Boolean = false
    var progressDialog: ProgressDialog? = null


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
            viewModel::onSequenceChanged,
            context = requireContext()
        )
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

        selectedList = mediaAdapter.getSelectedList()
    }

    private fun checkmultiselection(multiselection: Boolean) {
        if (multiselection) {
            constraintDel.visibility = View.VISIBLE
        } else {
            constraintDel.visibility = View.GONE
        }
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
        toolbar.setNavigationIcon(R.drawable.ic_back_black)
        toolbar.setNavigationOnClickListener { activity!!.onBackPressed() }

        mediaAdapter.setItemClick(this)

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

        btnplay.setOnClickListener {

            if (selectedList.size > 0) {
                val intent = Intent(requireContext(), PlayActivity::class.java)
                val bundle = Bundle()

                bundle.putParcelableArrayList("videoFileList", selectedList)
                intent.putExtras(bundle)
                startActivity(intent)
            } else {
                toast("please select a file to merge and play")
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
        progressDialog!!.setMessage("Playing please wait")
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()
    }

    override fun onSuccess(convertedFile: File, type: String) {
        toast("Success")
//        viewModel.editedImageFiles(convertedFile, playlistName)
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

    override fun onItemClick(multiselection: Boolean) {
        checkmultiselection(multiselection)
    }


}
