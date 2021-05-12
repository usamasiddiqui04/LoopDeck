package com.example.loopdeck.ui.collection.recents

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loopdeck.BitmapUtils
import com.example.loopdeck.DragData
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.drawer.AdvanceDrawerLayout
import com.example.loopdeck.editor.PlayActivity
import com.example.loopdeck.gallery.model.GalleryData
import com.example.loopdeck.gallery.view.PickerActivity
import com.example.loopdeck.onedrive.ItemFragment
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.ui.collection.move.MoveToPlaylistFragment
import com.example.loopdeck.ui.collection.playlist.PlaylistFragment
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import com.example.loopdeck.utils.extensions.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import kotlinx.android.synthetic.main.dailogbox.view.*
import kotlinx.android.synthetic.main.fragment_recents.*
import java.io.File
import java.util.*

class RecentsFragment : Fragment(), MediaAdaptor.OnItemClick {

    private var drawer: AdvanceDrawerLayout? = null
    private var selectedList = ArrayList<MediaData>()
    var string: MediaData? = null
    private var tagName: String = RecentsFragment::class.java.simpleName
    var multiSelection: Boolean = false
    var progressDialog: ProgressDialog? = null

    companion object {
        fun newInstance() = RecentsFragment()
        private const val REQUEST_RESULT_CODE = 100
    }

    private lateinit var viewModel: CollectionViewModel
    private val mediaAdapter by lazy {

        MediaAdaptor(mList = mutableListOf(), context = requireContext())
    }

    private fun checkmultiselection(multiselection: Boolean) {
        if (multiselection) {
            bottomLayout.visibility = View.VISIBLE
        } else {
            bottomLayout.visibility = View.GONE
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recents, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activityViewModelProvider()
        initViews()
        initObservers()

        selectedList = mediaAdapter.getSelectedList()
    }

    private fun initViews() {
        addfiles.setOnClickListener {
            val i = Intent(requireActivity(), PickerActivity::class.java)
            i.putExtra("IMAGES_LIMIT", 100)
            i.putExtra("VIDEOS_LIMIT", 100)
            i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
            i.putExtra("playlistName", "name")
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }

        progressDialog = ProgressDialog(requireContext())

//        ViewCompat.setLayoutDirection(drawer_layout!!, ViewCompat.LAYOUT_DIRECTION_RTL)

        mediaAdapter.setItemClick(this)
        recyclerview.adapter = mediaAdapter
        recyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        btnCreateRecents.setOnClickListener {
            savePlaylistNameDialog(requireContext()) {
                viewModel.createPlaylist(it, playlistName = null)
            }
        }

        btnDelete.setOnClickListener {
            deleteMediaFiles()

        }

        addmedia.setOnClickListener {
            val i = Intent(requireActivity(), PickerActivity::class.java)
            i.putExtra("IMAGES_LIMIT", 1000)
            i.putExtra("VIDEOS_LIMIT", 1000)
            i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
            i.putExtra("playlistName", "name")
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }

        fbAddPlaylist.setOnClickListener {
            savePlaylistNameDialog(requireContext()) {
                viewModel.createPlaylist(it, playlistName = null)
            }
        }

        btndublicate.setOnClickListener {

            selectedList.forEach { viewModel.dublicateMediafiles(it) }
            bottomLayout.visibility = View.GONE
            mediaAdapter.setSeletedList()
            mediaAdapter.notifyDataSetChanged()
        }

        btnmove.setOnClickListener {
            val moveToPlaylistFragment = MoveToPlaylistFragment()
            moveToPlaylistFragment.moveFileList(selectedList)
            showBottomSheetDialogFragment(moveToPlaylistFragment)
//            moveToPlaylistNameDialog(requireContext())
        }

        btnplay.setOnClickListener {
            val intent = Intent(requireContext(), PlayActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList("videoFileList", selectedList)
            bundle.putBoolean("isPublishedVideo", false)
            intent.putExtras(bundle)
            startActivity(intent)
        }

        initContainer()
    }

    fun deleteMediaFiles() {
        Handler(Looper.getMainLooper()).postDelayed({
            for (list in selectedList) {
                viewModel.delete(list)
            }
            selectedList.clear()
            multiSelection = false
            bottomLayout.visibility = View.GONE
        }, 1000)

        initObservers()

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUEST_RESULT_CODE && data != null) {
            val mediaList = data.getParcelableArrayListExtra<GalleryData>("MEDIA")
            if (mediaList!!.size > 1) {
                showPlaylistNameDialog(requireContext(), mediaList)
            } else {
                viewModel.addMediaFiles(mediaList)
            }
        }
    }

    private fun showPlaylistNameDialog(context: Context, mediaList: ArrayList<GalleryData>) {

        savePlaylistNameDialog(context) { file ->
            viewModel.createPlaylist(file, playlistName = null)
            viewModel.addMediaFiles(mediaList, file.name)
        }
    }


    private fun savePlaylistNameDialog(context: Context, onCreatePlaylist: (File) -> Unit) {

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dailogbox, null)

        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)

        val mAlertDialog = mBuilder.show()

        mDialogView.btn_okay.setOnClickListener {
            val name = mDialogView.txt_input.text.toString()
            val file = BitmapUtils.createPlatlist(requireContext(), name)
            onCreatePlaylist(file)
            mAlertDialog.dismiss()
        }

        mDialogView.btn_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun moveToPlaylistNameDialog(context: Context) {

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dailogbox, null)

        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("Enter Playlist Name")

        val mAlertDialog = mBuilder.show()

        mDialogView.btn_okay.setOnClickListener {
            val name = mDialogView.txt_input.text.toString()
            BitmapUtils.createPlatlist(requireContext(), name)
            selectedList.forEach {
                viewModel.dublicateMediafiles(it, name)
            }
            deleteMediaFiles()
            mAlertDialog.dismiss()
        }

        mDialogView.btn_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun initObservers() {


        viewModel.recentsMediaLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                if (list.isEmpty()) {
                    recyclerview.visibility = View.INVISIBLE
                    no_mediafile.visibility = View.VISIBLE
                    bottomLayout.visibility = View.GONE
                    return@Observer
                }
                recyclerview.visibility = View.VISIBLE
                no_mediafile.visibility = View.INVISIBLE
                addmedia.visibility = View.VISIBLE
                mediaAdapter.submitList(list.distinctBy { it.name })
            })

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
        multiSelection = false

        mediaAdapter.setSeletedList()
        mediaAdapter.notifyDataSetChanged()
    }

    private fun showBottomSheetDialogFragment(bottomSheetDialogFragment: BottomSheetDialogFragment) {
        val bundle = Bundle()
        bottomSheetDialogFragment.arguments = bundle
        fragmentManager?.let { bottomSheetDialogFragment.show(it, bottomSheetDialogFragment.tag) }
    }

    override fun onItemClick(multiselection: Boolean) {
        checkmultiselection(multiselection)
    }

}
