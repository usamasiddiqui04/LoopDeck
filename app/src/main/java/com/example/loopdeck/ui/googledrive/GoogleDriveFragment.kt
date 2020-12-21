package com.example.loopdeck.ui.googledrive

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
import com.example.loopdeck.BaseApplication
import com.example.loopdeck.DragData
import com.example.loopdeck.GoogleDriveMediaFiles
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaType
import com.example.loopdeck.googledrive.DriveQuickstart
import com.example.loopdeck.onedrive.ApiExplorer
import com.example.loopdeck.onedrive.DefaultCallback
import com.example.loopdeck.onedrive.ItemFragment
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.ui.collection.playlist.PlaylistFragment
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.FileList
import com.imagevideoeditor.PreviewVideoActivity
import com.loopdeck.photoeditor.EditImageActivity
import com.picker.gallery.model.GalleryData
import com.picker.gallery.view.PickerActivity
import com.xorbix.loopdeck.cameraapp.BitmapUtils
import kotlinx.android.synthetic.main.custom_layout.view.*
import kotlinx.android.synthetic.main.dailogbox.view.*
import kotlinx.android.synthetic.main.fragment_googlrdrive.*
import kotlinx.android.synthetic.main.fragment_recents.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class GoogleDriveFragment : Fragment() {

    companion object {
        fun newInstance() = GoogleDriveFragment()
    }

    var list: ArrayList<com.google.api.services.drive.model.File>? = null

    lateinit var viewModel: GoogleDriveViewModel


    private val googleDriveFileAdaptor by lazy {
        GoogleDriveFileAdaptor(mList = mutableListOf())
    }

    fun getfile() {
        val HTTP_TRANSPORT = com.google.api.client.http.javanet.NetHttpTransport()
        val service: Drive =
            Drive.Builder(
                HTTP_TRANSPORT,
                DriveQuickstart.JSON_FACTORY,
                DriveQuickstart.getCredentials(requireContext(), HTTP_TRANSPORT)
            )
                .setApplicationName(DriveQuickstart.APPLICATION_NAME)
                .build()
        val result: FileList = service.files().list()
            .setPageSize(100)
            .setFields("nextPageToken, files(id, name)")
            .execute()
        val files: List<com.google.api.services.drive.model.File>? = result.files
        if (files == null || files.isEmpty()) {
            println("No files found.")
        } else {
            println("Files:")
            list!!.addAll(files)
            for (file in files) {

                //TODO: Get thumbnail link here and assign it to imageView
                file.thumbnailLink

//                    println("${file.getName()} ${file.getId()} ${file.fileExtension}")
                println(file.toString())

            }
        }


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
        getfile()
        initObservers()

    }


    private fun initViews() {


        googleDriveRecyclerview?.adapter = googleDriveFileAdaptor
        googleDriveRecyclerview?.layoutManager = GridLayoutManager(requireContext(), 3)

    }


    private fun initObservers() {
        viewModel.recentsMediaLiveData.observe(viewLifecycleOwner, { recentsList ->
            googleDriveFileAdaptor.submitList(recentsList.distinctBy { it.name })
        })

    }



}