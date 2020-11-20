package com.example.loopdeck.ui.collection.playlist

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.loopdeck.DragData
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaType
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.utils.callbacks.ItemMoveCallback
import com.loopdeck.photoeditor.EditImageActivity
import com.obs.marveleditor.MainActivity
import kotlinx.android.synthetic.main.fragment_playlist.*


class PlaylistFragment : Fragment() {

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

        private const val REQUEST_STORAGE_PERMISSION = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            viewModel.importedFilesIntent = data
            if (data?.clipData?.itemCount ?: 0 > 1) {
                viewModel.addMediaFiles(playlistName)

            } else {
                viewModel.addMediaFiles(playlistName)

            }

        }

    }

    private lateinit var viewModel: CollectionViewModel

    private val mediaAdapter by lazy {
        MediaAdaptor(
            mutableListOf(),
            onItemClickListener,
            onItemLongClickListener,
            viewModel::onSequenceChanged
        )
    }


    private val onItemLongClickListener: (View, MediaData) -> Boolean = { itemView, mediaData ->
        val state = DragData(mediaData, itemView.width, itemView.height)
        val shadow: View.DragShadowBuilder = View.DragShadowBuilder(itemView)
        ViewCompat.startDragAndDrop(itemView, null, shadow, state, 0)
    }


    private val onItemClickListener: (MediaData) -> Unit = { mediaData ->
        Toast.makeText(requireContext(), "Item clicked $mediaData", Toast.LENGTH_SHORT).show()

        when (mediaData.mediaType) {
            MediaType.IMAGE -> {

                val intent = Intent(requireContext(), EditImageActivity::class.java)
                intent.putExtra("imagePath", mediaData.filePath)
                startActivity(intent)
            }
            MediaType.VIDEO -> {
                val intent = Intent(requireContext(), MainActivity::class.java)
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
        return inflater.inflate(R.layout.fragment_playlist, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CollectionViewModel::class.java)
        initViews()
        initObservers()
    }


    private fun initViews() {

        var touchHelper: ItemTouchHelper? = null
        recyclerview?.adapter = mediaAdapter
        recyclerview?.layoutManager = GridLayoutManager(requireContext(), 3)
        val callback = ItemMoveCallback(mediaAdapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerview)


        btnGallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {

                // If you do not have permission, request it
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Companion.REQUEST_STORAGE_PERMISSION
                )
            } else {
                val pickPhoto = Intent(
                    Intent.ACTION_GET_CONTENT,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                pickPhoto.type = "image/* video/*"
                startActivityForResult(pickPhoto, 1)
            }
        }

        initContainer()
    }


    private fun initObservers() {
        playlistName?.let {
            viewModel.getPlaylistMedia(it).observe(viewLifecycleOwner, {
                mediaAdapter.submitList(it)
            })
        }
    }


    private fun initContainer() {
        frameDelete.setOnDragListener { view, dragEvent ->
            when (dragEvent.action) {
                DragEvent.ACTION_DRAG_ENTERED -> frameDelete.setBackgroundColor(Color.GREEN)
                DragEvent.ACTION_DRAG_EXITED -> frameDelete.setBackgroundColor(Color.RED)
                DragEvent.ACTION_DRAG_ENDED -> frameDelete.setBackgroundColor(Color.WHITE)
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