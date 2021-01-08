package com.example.loopdeck.ui.googledrive

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loopdeck.R
import com.example.loopdeck.ui.adapters.GoogleDriveFileAdaptor
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import com.example.loopdeck.utils.extensions.toast
import com.google.api.services.drive.model.File
import kotlinx.android.synthetic.main.fragment_googlrdrive.*

class GoogleDriveFragment : Fragment() {

    companion object {
        fun newInstance() = GoogleDriveFragment()
    }


    lateinit var viewModel: GoogleDriveViewModel


    private val googleDriveFileAdaptor by lazy {
        GoogleDriveFileAdaptor(mList = mutableListOf(), onItemClickListener)
    }

    private val onItemClickListener: (File) -> Unit = { mediaData ->
        toast(mediaData.id.toString())

            viewModel.download(mediaData.id)

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
        viewModel = activityViewModelProvider()
        initViews()
        initObservers()

        viewModel.getDrivefiles()
    }


    private fun initViews() {
        googleDriveRecyclerview?.adapter = googleDriveFileAdaptor
        googleDriveRecyclerview?.layoutManager = GridLayoutManager(requireContext(), 1)

    }


    private fun initObservers() {
        viewModel.recentsMediaLiveData.observe(viewLifecycleOwner, { recentsList ->
            googleDriveFileAdaptor.submitList(recentsList.distinctBy { it.name })
        })

    }


}