package com.example.loopdeck.ui.googledrive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loopdeck.R
import com.example.loopdeck.imageloader.GlideImageLoader
import com.example.loopdeck.ui.adapters.GoogleDriveAdaptor
import com.example.loopdeck.utils.extensions.toast
import com.google.api.services.drive.model.File
import kotlinx.android.synthetic.main.fragment_googlrdrive.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class GoogleDriveFragment : Fragment() {

    companion object {
        fun newInstance() = GoogleDriveFragment()
    }


    val job = Job()

    val ioScope = CoroutineScope(Dispatchers.IO + job)

    val imageLoader by lazy {
        GlideImageLoader(requireContext())
    }

    private val googleDriveFileAdaptor by lazy {
        GoogleDriveAdaptor(
            imageLoader,
            mList = mutableListOf(),
            itemClickListener = onItemClickListener
        )
    }

    private val onItemClickListener: (File) -> Unit = { mediaData ->
        toast(mediaData.id.toString())

        GoogleDriveController.download(requireContext(), ioScope, mediaData)

//        val intent = Intent(requireContext(), EditImageActivity::class.java)
//        intent.putExtra("imagePath", mediaData.name)
//        startActivity(intent)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_googlrdrive, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViews()
        initObservers()
        GoogleDriveController.init(requireActivity().application)
        GoogleDriveController.getDrivefiles()
    }


    private fun initViews() {
        googleDriveRecyclerview?.adapter = googleDriveFileAdaptor
        googleDriveRecyclerview?.layoutManager = GridLayoutManager(requireContext(), 4)

    }


    private fun initObservers() {
        GoogleDriveController.googleDriveFilesLiveData.observe(
            viewLifecycleOwner,
            Observer { recentsList ->
                googleDriveFileAdaptor.submitList(recentsList.distinctBy { it.name })
            })

    }


}