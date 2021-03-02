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
import com.imagevideoeditor.PreviewVideoActivity
import com.loopdeck.photoeditor.EditImageActivity
import com.example.loopdeck.gallery.model.GalleryData
import com.example.loopdeck.gallery.view.PickerActivity
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
    private var selectedList = ArrayList<MediaData>()
    var mediaData: MediaData? = null
    var multiSelection: Boolean = false

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
                        val intent = Intent(requireContext(), EditImageActivity::class.java)
                        intent.putExtra("imagePath", mediadata.filePath)
                        startActivity(intent)
                    }
                    MediaType.VIDEO -> {
                        val intent = Intent(requireContext(), PreviewVideoActivity::class.java)
                        intent.putExtra("videoPath", mediadata.filePath)
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
