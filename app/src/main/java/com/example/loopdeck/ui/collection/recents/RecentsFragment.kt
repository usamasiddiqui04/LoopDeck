package com.example.loopdeck.ui.collection.recents

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loopdeck.DragData
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaType
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.ui.collection.playlist.PlaylistFragment
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import com.imagevideoeditor.PreviewVideoActivity
import com.loopdeck.photoeditor.EditImageActivity
import com.obs.marveleditor.MainActivity
import com.picker.gallery.model.GalleryData
import com.picker.gallery.view.PickerActivity
import com.xorbix.loopdeck.cameraapp.BitmapUtils
import kotlinx.android.synthetic.main.dailogbox.view.*
import kotlinx.android.synthetic.main.fragment_recents.*
import java.io.File
import java.util.*

class RecentsFragment : Fragment() {

    companion object {
        fun newInstance() = RecentsFragment()
        private const val REQUEST_RESULT_CODE = 100
    }


    private lateinit var viewModel: CollectionViewModel

    private val mediaAdapter by lazy {
        MediaAdaptor(mList = mutableListOf(), onItemClickListener, onItemLongClickListener)
    }

    private val onItemLongClickListener: (View, MediaData) -> Boolean = { itemView, mediaData ->
        val state = DragData(mediaData, itemView.width, itemView.height)
        val shadow: View.DragShadowBuilder = View.DragShadowBuilder(itemView)
        ViewCompat.startDragAndDrop(itemView, null, shadow, state, 0)
    }
    private val onItemClickListener: (MediaData) -> Unit = { mediaData ->


        when (mediaData.mediaType) {
            MediaType.IMAGE -> {

                val intent = Intent(requireContext(), EditImageActivity::class.java)
                intent.putExtra("imagePath", mediaData.filePath)
                startActivity(intent)
            }
            MediaType.VIDEO -> {
                val intent = Intent(requireContext(), PreviewVideoActivity::class.java)
                intent.putExtra("videoPath", mediaData.filePath)
                startActivity(intent)
            }
            else -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PlaylistFragment.newInstance(mediaData.name))
                    .addToBackStack(null)
                    .commit()
            }
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

    }


    private fun initViews() {


        recyclerview?.adapter = mediaAdapter
        recyclerview?.layoutManager = GridLayoutManager(requireContext(), 3)

        btnCreate.setOnClickListener {
            savePlaylistNameDialog(requireContext()) {
                viewModel.createPlaylist(it)
            }
        }

        btnGallery.setOnClickListener {

            val i = Intent(activity, PickerActivity::class.java)
            i.putExtra("IMAGES_LIMIT", 100)
            i.putExtra("VIDEOS_LIMIT", 100)
            i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }
        initContainer()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUEST_RESULT_CODE && data != null) {
            val mediaList = data.getParcelableArrayListExtra<GalleryData>("MEDIA")
            if (mediaList.size > 1) {
                showPlaylistNameDialog(requireContext(), mediaList)
            } else {
                viewModel.addMediaFiles(mediaList)
            }
        }
    }

    private fun showPlaylistNameDialog(context: Context, mediaList: ArrayList<GalleryData>) {
        savePlaylistNameDialog(context) {
            viewModel.createPlaylist(it)
            viewModel.addMediaFiles(mediaList, it.name)
        }
    }


    private fun savePlaylistNameDialog(context: Context, onCreatePlaylist: (File) -> Unit) {

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dailogbox, null)

        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("Enter Playlist Name")

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

    private fun initObservers() {
        viewModel.recentsMediaLiveData.observe(viewLifecycleOwner, { recentsList ->
            mediaAdapter.submitList(recentsList.distinctBy { it.name })
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
    }

}