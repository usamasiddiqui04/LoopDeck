package com.example.loopdeck.ui.collection.playlist

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.utils.callbacks.ItemMoveCallback
import com.loopdeck.photoeditor.EditImageActivity
import com.obs.marveleditor.MainActivity
import com.picker.gallery.model.GalleryData
import com.picker.gallery.view.PickerActivity
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.fragment_playlist.btnDelete
import kotlinx.android.synthetic.main.fragment_playlist.btnGallery
import kotlinx.android.synthetic.main.fragment_playlist.recyclerview
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*
import kotlinx.android.synthetic.main.item_recent_folder_list.view.selectitem
import kotlinx.android.synthetic.main.item_recent_list_images.view.*
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*
import java.util.ArrayList


class PlaylistFragment : Fragment() {

    private var Selectlist = ArrayList<MediaData>()
    private var viewholder: RecyclerView.ViewHolder? = null
    var filepath: String? = null

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
            viewholder = viewHolder
            val string = list.get(viewHolder.adapterPosition)

            when (mediadata.mediaType) {
                MediaType.IMAGE -> {

                    if (viewHolder.itemView.selectitem.visibility == View.GONE) {
                        viewHolder.itemView.selectitem.visibility = View.VISIBLE
                        viewHolder.itemView.cardview.alpha = 0.5f
                        Selectlist.add(string)
                    }

                }
                MediaType.VIDEO -> {
                    if (viewHolder.itemView.selectitem.visibility == View.GONE) {
                        viewHolder.itemView.selectitem.visibility = View.VISIBLE
                        viewHolder.itemView.cardvideo.alpha = 0.5f
                        Selectlist.add(string)
                    }
                }
                else -> {
                    if (viewHolder.itemView.selectitem.visibility == View.GONE) {
                        viewHolder.itemView.selectitem.visibility = View.VISIBLE
                        viewHolder.itemView.cardfolder.alpha = 0.5f
                        Selectlist.add(string)
                    }
                }
            }

        }


    private val onItemClickListener: (MediaData) -> Unit = { mediaData ->
//        Toast.makeText(requireContext(), "Item clicked $mediaData", Toast.LENGTH_SHORT).show()

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
        playlistname.text = playlistName
        var touchHelper: ItemTouchHelper? = null
        recyclerview?.adapter = mediaAdapter
        recyclerview?.layoutManager = GridLayoutManager(requireContext(), 2)
        val callback = ItemMoveCallback(mediaAdapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerview)


        btnGallery.setOnClickListener {

            val i = Intent(activity, PickerActivity::class.java)
            i.putExtra("IMAGES_LIMIT", 100)
            i.putExtra("VIDEOS_LIMIT", 100)
            i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }

        btnDelete.setOnClickListener {
            for (list in Selectlist) {
                viewModel.delete(list)
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


}
