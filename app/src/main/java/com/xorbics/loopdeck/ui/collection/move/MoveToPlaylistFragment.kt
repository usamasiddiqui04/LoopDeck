package com.xorbics.loopdeck.ui.collection.move

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.xorbics.loopdeck.BitmapUtils
import com.xorbics.loopdeck.R
import com.xorbics.loopdeck.data.MediaData
import com.xorbics.loopdeck.ui.adapters.MoveToPlaylistAdaptor
import com.xorbics.loopdeck.utils.extensions.activityViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_move_to_playlist.*
import java.util.*

class MoveToPlaylistFragment : BottomSheetDialogFragment() {

    private lateinit var viewModel: MoveToPlaylistViewModel
    private var selectedList = ArrayList<MediaData>()

    private val moveToPlaylistAdaptor by lazy {
        MoveToPlaylistAdaptor(
            mList = mutableListOf(),
            onItemClickListener
        )
    }

    private val onItemClickListener: (MediaData) -> Unit =
        { mediaData ->

            BitmapUtils.createPlatlist(requireContext(), mediaData.name)
            selectedList.forEach {
                viewModel.dublicateMediafiles(it, mediaData.name)
            }
            deleteMediaFiles()
            dialog!!.dismiss()

        }

    fun deleteMediaFiles() {
        Handler(Looper.getMainLooper()).postDelayed({
            for (list in selectedList) {
                viewModel.delete(list)
            }
        }, 1000)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_move_to_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = activityViewModelProvider()

        recyclerViewMoveToPlaylist.adapter = moveToPlaylistAdaptor
        recyclerViewMoveToPlaylist.layoutManager = LinearLayoutManager(context)

        close.setOnClickListener {
            dismiss()
        }

        initObservers()
    }

    private fun initObservers() {

        viewModel.recentsPlaylistLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                moveToPlaylistAdaptor.submitList(list.distinctBy { it.name })
            })


    }

    fun moveFileList(moveList: ArrayList<MediaData>) {
        selectedList.clear()
        selectedList = moveList
    }
}