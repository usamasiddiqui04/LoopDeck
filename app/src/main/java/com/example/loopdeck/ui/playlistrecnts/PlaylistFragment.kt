package com.example.loopdeck.ui.playlistrecnts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.loopdeck.R
import com.example.loopdeck.ui.ItemMoveCallbackPlaylist
import com.example.loopdeck.ui.adapters.PlayListRecentViewAdaptor
import com.example.loopdeck.ui.recents.RecentsFragment
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import com.loopdeck.photoeditor.EditImageActivity
import com.obs.marveleditor.MainActivity
import kotlinx.android.synthetic.main.dailogbox.view.*
import kotlinx.android.synthetic.main.fragment_recents.*
import java.io.File


class PlaylistFragment : Fragment() {

    val playListPath by lazy {
        arguments?.getString(KEY_PATH)
    }

    companion object {
        private const val KEY_PATH = "key_path"
        fun newInstance(playlistPath: String) = PlaylistFragment().apply {
            val args = Bundle()
            args.putString(KEY_PATH, playlistPath)
            arguments = args
        }
        private const val REQUEST_STORAGE_PERMISSION = 1
    }

    val recentsPlaylistViewAdaptor by lazy {
        PlayListRecentViewAdaptor(mutableListOf(), onItemClickListener)
    }

    private val onItemClickListener: (File) -> Unit = { item ->
        Toast.makeText(requireContext(), "Item clicked ${item.name}", Toast.LENGTH_SHORT).show()

        if (item.isImage()) {
            startActivity(
                Intent(
                    requireContext(),
                    EditImageActivity::class.java
                )
            )
        } else if (item.isVideo()) {
            startActivity(
                Intent(
                    requireContext(),
                    MainActivity::class.java
                )
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            viewModel.importedFilesIntent = data
            if (data?.clipData?.itemCount ?: 0 > 1) {
                viewModel.playlistName.value = playListPath
                viewModel.importMediaFiles(requireContext())
                viewModel.loadRecentList(requireContext())
            } else {
                viewModel.playlistName.value = playListPath
                viewModel.importMediaFiles(requireContext())
                viewModel.loadRecentList(requireContext())
            }

        }

    }


    private lateinit var viewModel: PlaylistRecntsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.playlist_recnts_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PlaylistRecntsViewModel::class.java)
        initViews()
        initObservers()
        viewModel.loadRecentList(requireContext())
        // TODO: Use the ViewModel
    }

    private fun initViews() {
        var touchHelper: ItemTouchHelper? = null
        viewModel.playlistName.value = playListPath.toString()
        recyclerview?.adapter = recentsPlaylistViewAdaptor
        recyclerview?.layoutManager = GridLayoutManager(requireContext(), 3)
        val callback: ItemTouchHelper.Callback = ItemMoveCallbackPlaylist(recentsPlaylistViewAdaptor)
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
                    PlaylistFragment.REQUEST_STORAGE_PERMISSION
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
    }

    private fun initObservers() {

        viewModel.recentsMediaList.observe(viewLifecycleOwner, { recentsList ->
            recentsPlaylistViewAdaptor.submitList(recentsList)
        })
    }

//    var itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
//        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
//        0
//    ) {
//        override fun onMove(
//            recyclerView: RecyclerView,
//            viewHolder: RecyclerView.ViewHolder,
//            target: RecyclerView.ViewHolder
//        ): Boolean {
//            val position_dragged = viewHolder.adapterPosition
//            val position_target = target.adapterPosition
//            Collections.swap(
//                listOf(viewModel.recentsMediaList),
//                position_dragged,
//                position_target
//            )
//            recentsPlaylistViewAdaptor.notifyItemMoved(position_dragged, position_target)
//            return false
//        }
//
//
//        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
//    })
//
}