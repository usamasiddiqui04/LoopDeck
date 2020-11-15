package com.example.loopdeck.ui.playlistrecnts

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.R
import com.example.loopdeck.ui.adapters.PlayListRecentViewAdaptor
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import com.loopdeck.photoeditor.EditImageActivity
import com.obs.marveleditor.MainActivity
import kotlinx.android.synthetic.main.fragment_recents.*
import java.io.File
import java.util.*

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

        viewModel.playlistName.value = playListPath.toString()
        recyclerview?.adapter = recentsPlaylistViewAdaptor
        recyclerview?.layoutManager = GridLayoutManager(requireContext(), 3)
        itemTouchHelper.attachToRecyclerView(recyclerview)
    }

    private fun initObservers() {

        viewModel.recentsMediaList.observe(viewLifecycleOwner, { recentsList ->
            recentsPlaylistViewAdaptor.submitList(recentsList)
        })
    }

    var itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
        0
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val position_dragged = viewHolder.adapterPosition
            val position_target = target.adapterPosition
            Collections.swap(listOf(viewModel.loadRecentList(requireContext())), position_dragged, position_target)
            recentsPlaylistViewAdaptor.notifyItemMoved(position_dragged, position_target)
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
    })

}