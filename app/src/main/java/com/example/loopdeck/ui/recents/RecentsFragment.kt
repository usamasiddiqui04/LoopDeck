package com.example.loopdeck.ui.recents

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.loopdeck.R
import com.example.loopdeck.ui.adapters.RecentsViewAdaptor
import com.example.loopdeck.ui.playlistrecnts.PlaylistFragment
import com.example.loopdeck.utils.isImage
import com.example.loopdeck.utils.isVideo
import com.loopdeck.photoeditor.EditImageActivity
import com.xorbix.loopdeck.cameraapp.BitmapUtils
import kotlinx.android.synthetic.main.dailogbox.view.*
import kotlinx.android.synthetic.main.fragment_recents.*
import java.io.File

class RecentsFragment : Fragment() {

    companion object {
        fun newInstance() = RecentsFragment()
        private const val REQUEST_STORAGE_PERMISSION = 1
    }

    val recentsViewAdaptor by lazy {
        RecentsViewAdaptor(mutableListOf(), onItemClickListener)
    }

    private val onItemClickListener: (File) -> Unit = { item ->
        Toast.makeText(requireContext(), "Item clicked ${item.name}", Toast.LENGTH_SHORT).show()

        when {
            item.isImage() -> {
                startActivity(
                    Intent(
                        requireContext(),
                        EditImageActivity::class.java
                    )
                )
            }
            item.isVideo() -> {
                startActivity(
                    Intent(
                        requireContext(),
                        com.obs.marveleditor.MainActivity::class.java
                    )
                )
            }
            else -> {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PlaylistFragment.newInstance(item.name))
                    .addToBackStack(null)
                    .commit()
            }
        }
    }


    private lateinit var viewModel: RecentsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recents, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            viewModel.importedFilesIntent = data
            if (data?.clipData?.itemCount ?: 0 > 1) {
                showPlaylistNameDialog(requireContext())
            } else {
                viewModel.importMediaFiles(requireContext())
                viewModel.loadRecentList(requireContext())
            }

        }

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(RecentsViewModel::class.java)
        initViews()
        initObservers()
        viewModel.loadRecentList(requireContext())
    }


    private fun initViews() {


        recyclerview?.adapter = recentsViewAdaptor
        recyclerview?.layoutManager = GridLayoutManager(requireContext(), 3)

        btnpalylist.setOnClickListener {
            SavePlaylistNameDialog(requireContext())
        }

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
    }

    fun showPlaylistNameDialog(context: Context) {

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dailogbox, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("Enter Playlist Name")
        //show dialog
        val mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.btn_okay.setOnClickListener {
            //dismiss dialog
            viewModel.playlistName.value = mDialogView.txt_input.text.toString()
            viewModel.importMediaFiles(requireContext())
            viewModel.loadRecentList(requireContext())
            mAlertDialog.dismiss()

        }
        //cancel button click of custom layout
        mDialogView.btn_cancel.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }

    fun SavePlaylistNameDialog(context: Context) {

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dailogbox, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("Enter Playlist Name")
        //show dialog
        val mAlertDialog = mBuilder.show()
        //login button click of custom layout
        mDialogView.btn_okay.setOnClickListener {
            //dismiss dialog
            BitmapUtils.createPlatlist(requireContext(), mDialogView.txt_input.text.toString())
            viewModel.loadRecentList(requireContext())
            mAlertDialog.dismiss()

        }
        //cancel button click of custom layout
        mDialogView.btn_cancel.setOnClickListener {
            //dismiss dialog
            mAlertDialog.dismiss()
        }
    }

    private fun initObservers() {

        viewModel.recentsMediaList.observe(viewLifecycleOwner, { recentsList ->
            recentsViewAdaptor.submitList(recentsList)
        })

//        myimageFile = viewModel.findImage(
//            (File(requireContext().getExternalFilesDir(null)!!.absolutePath)),
//            "/Loopdeck Media Files'",
//            requireContext()
//        );
    }

}