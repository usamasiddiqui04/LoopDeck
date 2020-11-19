package com.example.loopdeck.ui.collection.recents

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaType
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.ui.collection.playlist.PlaylistFragment
import com.example.loopdeck.utils.callbacks.ItemMoveCallback
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import com.loopdeck.photoeditor.EditImageActivity
import com.obs.marveleditor.MainActivity
import com.xorbix.loopdeck.cameraapp.BitmapUtils
import kotlinx.android.synthetic.main.dailogbox.view.*
import kotlinx.android.synthetic.main.fragment_recents.*
import java.io.File

class RecentsFragment : Fragment() {

    companion object {
        fun newInstance() = RecentsFragment()
        private const val REQUEST_STORAGE_PERMISSION = 1
    }

    private lateinit var viewModel: CollectionViewModel

    private val mediaAdapter by lazy {
        MediaAdaptor(mutableListOf(), onItemClickListener)
    }

    private val onItemClickListener: (MediaData) -> Unit = { mediaData ->

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

            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activityViewModelProvider()
        initViews()
        initObservers()

    }


    private fun initViews() {


        var touchHelper: ItemTouchHelper? = null
        recyclerview?.adapter = mediaAdapter
        recyclerview?.layoutManager = GridLayoutManager(requireContext(), 3)
        val callback: ItemTouchHelper.Callback = ItemMoveCallback(mediaAdapter)
        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(recyclerview)

        btnDelete.setOnClickListener {
            savePlaylistNameDialog(requireContext()) {
                viewModel.createPlaylist(it)
            }
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

    private fun showPlaylistNameDialog(context: Context) {
        savePlaylistNameDialog(context) {
            viewModel.createPlaylist(it)
            viewModel.importMediaFiles(requireContext(), it.name)
        }
    }


    private fun savePlaylistNameDialog(context: Context, onCreatePlaylist: (File) -> Unit) {

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dailogbox, null)

        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("Enter Playlist Name")

        val mAlertDialog = mBuilder.show()

        mDialogView.btn_okay.setOnClickListener {
            val name = mDialogView.txt_input.text.toString()
            val file = BitmapUtils.createPlatlist(requireContext(), name)
            onCreatePlaylist(file)
            mAlertDialog.dismiss()
        }

        mDialogView.btn_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun initObservers() {


        viewModel.recentsMediaLiveData.observe(viewLifecycleOwner, { recentsList ->
            mediaAdapter.submitList(recentsList)
        })

    }

}