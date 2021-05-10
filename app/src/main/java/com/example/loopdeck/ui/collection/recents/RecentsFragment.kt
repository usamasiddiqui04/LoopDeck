package com.example.loopdeck.ui.collection.recents

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.BitmapUtils
import com.example.loopdeck.DragData
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaType
import com.example.loopdeck.drawer.AdvanceDrawerLayout
import com.example.loopdeck.editor.PlayActivity
import com.example.loopdeck.editor.PreviewPhotoActivity
import com.example.loopdeck.editor.PreviewVideoActivity
import com.example.loopdeck.gallery.model.GalleryData
import com.example.loopdeck.gallery.view.PickerActivity
import com.example.loopdeck.onedrive.ItemFragment
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.ui.collection.move.MoveToPlaylistFragment
import com.example.loopdeck.ui.collection.playlist.PlaylistActivity
import com.example.loopdeck.ui.collection.playlist.PlaylistFragment
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import com.example.loopdeck.utils.extensions.toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import kotlinx.android.synthetic.main.activity_recents.*
import kotlinx.android.synthetic.main.dailogbox.view.*
import kotlinx.android.synthetic.main.fragment_playlist.*
import kotlinx.android.synthetic.main.fragment_recents.*
import kotlinx.android.synthetic.main.fragment_recents.bottomLayout
import kotlinx.android.synthetic.main.fragment_recents.btnDelete
import kotlinx.android.synthetic.main.fragment_recents.btnplay
import kotlinx.android.synthetic.main.fragment_recents.recyclerview
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*
import kotlinx.android.synthetic.main.item_recent_folder_list.view.selectitem
import kotlinx.android.synthetic.main.item_recent_list_images.view.*
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*
import java.io.File
import java.util.*

class RecentsFragment : Fragment(),
    OptiFFMpegCallback {

    private var drawer: AdvanceDrawerLayout? = null
    private var selectedList = ArrayList<MediaData>()
    var string: MediaData? = null
    private var tagName: String = RecentsFragment::class.java.simpleName
    var multiSelection: Boolean = false
    var progressDialog: ProgressDialog? = null

    companion object {
        fun newInstance() = RecentsFragment()
        private const val REQUEST_RESULT_CODE = 100
    }

    private lateinit var viewModel: CollectionViewModel
    private val mediaAdapter by lazy {
        MediaAdaptor(
            mList = mutableListOf(),
            itemClickListener = onItemClickListener, itemLongClickListener = onItemLongClickListener
        )
    }

    private fun checkmultiselection() {
        if (multiSelection) {
            bottomLayout.visibility = View.VISIBLE
        } else {
            bottomLayout.visibility = View.GONE
        }
    }

    private val onItemLongClickListener: (View, RecyclerView.ViewHolder, MutableList<MediaData>, MediaData) -> Unit =
        { itemView, viewHolder, list, mediadata ->

            multiSelection = true
            checkmultiselection()
            toggleSelection(viewHolder, mediadata, list)

        }

    private fun toggleSelection(
        viewHolder: RecyclerView.ViewHolder,
        mediadata: MediaData,
        list: MutableList<MediaData>
    ) {
        string = list[viewHolder.adapterPosition]
        when (mediadata.mediaType) {
            MediaType.IMAGE -> {

                if (viewHolder.itemView.selectitem.visibility == View.GONE) {
                    viewHolder.itemView.selectitem.visibility = View.VISIBLE
                    viewHolder.itemView.cardview.alpha = 0.5f
                    selectedList.add(string!!)

                } else {
                    viewHolder.itemView.selectitem.visibility = View.GONE
                    viewHolder.itemView.cardview.alpha = 1f
                    selectedList.remove(string!!)
                }
            }
            MediaType.VIDEO -> {
                if (viewHolder.itemView.selectitem.visibility == View.GONE) {
                    viewHolder.itemView.selectitem.visibility = View.VISIBLE
                    viewHolder.itemView.cardvideo.alpha = 0.5f
                    selectedList.add(string!!)
                } else {
                    viewHolder.itemView.selectitem.visibility = View.GONE
                    viewHolder.itemView.cardvideo.alpha = 1f
                    selectedList.remove(string!!)
                }
            }
            else -> {
                if (viewHolder.itemView.selectitem.visibility == View.GONE) {
                    viewHolder.itemView.selectitem.visibility = View.VISIBLE
                    viewHolder.itemView.cardfolder.alpha = 0.5f
                    selectedList.add(string!!)
                } else {
                    viewHolder.itemView.selectitem.visibility = View.GONE
                    viewHolder.itemView.cardfolder.alpha = 1f
                    selectedList.remove(string!!)
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
                        val intent = Intent(requireContext(), PreviewPhotoActivity::class.java)
                        intent.putExtra("mediaData", mediadata)
                        startActivity(intent)
                    }
                    MediaType.VIDEO -> {
                        val intent = Intent(requireContext(), PreviewVideoActivity::class.java)
                        intent.putExtra("mediaData", mediadata)
                        startActivity(intent)

                    }
                    else -> {

                        val intent = Intent(requireContext(), PlaylistActivity::class.java)
                        intent.putExtra("mediaData", mediadata)
                        startActivity(intent)
                    }
                }
            } else {

                toggleSelection(viewHolder, mediadata, list)
                multiSelection = !selectedList.isEmpty()
                if (!multiSelection) {
                    checkmultiselection()
                }
            }


        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_recents, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = activityViewModelProvider()
        initViews()
        initObservers()

    }


    private fun navigateToRoot() {
        requireFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment, ItemFragment.newInstance("root"))
            .addToBackStack(null)
            .commit()
    }

    private fun initViews() {
        addfiles.setOnClickListener {
            val i = Intent(requireActivity(), PickerActivity::class.java)
            i.putExtra("IMAGES_LIMIT", 100)
            i.putExtra("VIDEOS_LIMIT", 100)
            i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
            i.putExtra("playlistName", "")
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }

        progressDialog = ProgressDialog(requireContext())

//        ViewCompat.setLayoutDirection(drawer_layout!!, ViewCompat.LAYOUT_DIRECTION_RTL)

        recyclerview.adapter = mediaAdapter
        recyclerview.layoutManager = GridLayoutManager(requireContext(), 3)
        btnCreateRecents.setOnClickListener {
            savePlaylistNameDialog(requireContext()) {
                viewModel.createPlaylist(it)
            }
        }

        btnDelete.setOnClickListener {
            deleteMediaFiles()

        }

        addmedia.setOnClickListener {
            val i = Intent(requireActivity(), PickerActivity::class.java)
            i.putExtra("IMAGES_LIMIT", 1000)
            i.putExtra("VIDEOS_LIMIT", 1000)
            i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
            i.putExtra("playlistName", "")
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }

        fbAddPlaylist.setOnClickListener {
            savePlaylistNameDialog(requireContext()) {
                viewModel.createPlaylist(it)
            }
        }

        btndublicate.setOnClickListener {

            selectedList.forEach {
                viewModel.dublicateMediafiles(it)
            }
            bottomLayout.visibility = View.GONE
        }

        btnmove.setOnClickListener {

            val moveToPlaylistFragment = MoveToPlaylistFragment()
            moveToPlaylistFragment.moveFileList(selectedList)

            showBottomSheetDialogFragment(moveToPlaylistFragment)
//            moveToPlaylistNameDialog(requireContext())
        }

        btnplay.setOnClickListener {


            val intent = Intent(requireContext(), PlayActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList("videoFileList", selectedList)
            intent.putExtras(bundle)
            startActivity(intent)


        }
        initContainer()
    }

    fun deleteMediaFiles() {
        Handler(Looper.getMainLooper()).postDelayed({
            for (list in selectedList) {
                viewModel.delete(list)
            }
            selectedList.clear()
            multiSelection = false
            bottomLayout.visibility = View.GONE
        }, 1000)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQUEST_RESULT_CODE && data != null) {
            val mediaList = data.getParcelableArrayListExtra<GalleryData>("MEDIA")
            if (mediaList!!.size > 1) {
                showPlaylistNameDialog(requireContext(), mediaList)
            } else {
                viewModel.addMediaFiles(mediaList)
            }
        }
    }

    private fun showPlaylistNameDialog(context: Context, mediaList: ArrayList<GalleryData>) {

        savePlaylistNameDialog(context) { file ->
            viewModel.createPlaylist(file)
            viewModel.addMediaFiles(mediaList, file.name)
        }
    }


    private fun savePlaylistNameDialog(context: Context, onCreatePlaylist: (File) -> Unit) {

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dailogbox, null)

        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)

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

    private fun moveToPlaylistNameDialog(context: Context) {

        val mDialogView = LayoutInflater.from(context).inflate(R.layout.dailogbox, null)

        val mBuilder = AlertDialog.Builder(context)
            .setView(mDialogView)
            .setTitle("Enter Playlist Name")

        val mAlertDialog = mBuilder.show()

        mDialogView.btn_okay.setOnClickListener {
            val name = mDialogView.txt_input.text.toString()
            BitmapUtils.createPlatlist(requireContext(), name)
            selectedList.forEach {
                viewModel.dublicateMediafiles(it, name)
            }
            deleteMediaFiles()
            mAlertDialog.dismiss()
        }

        mDialogView.btn_cancel.setOnClickListener {
            mAlertDialog.dismiss()
        }
    }

    private fun initObservers() {


        viewModel.recentsMediaLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                if (list.isEmpty()) {
                    recyclerview.visibility = View.INVISIBLE
                    no_mediafile.visibility = View.VISIBLE
                    bottomLayout.visibility = View.GONE
                    return@Observer
                }
                recyclerview.visibility = View.VISIBLE
                no_mediafile.visibility = View.INVISIBLE
                addmedia.visibility = View.VISIBLE
                mediaAdapter.submitList(list.distinctBy { it.name })
            })

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
        multiSelection = false
    }

    override fun onProgress(progress: String) {
        progressDialog!!.setMessage("Playing please wait")
        progressDialog!!.setCanceledOnTouchOutside(false)
        progressDialog!!.show()
    }

    override fun onSuccess(convertedFile: File, type: String) {
        toast("Success")
        val intent = Intent(requireContext(), PlayActivity::class.java)
        intent.putExtra("videoFilePath", convertedFile.absolutePath)
        startActivity(intent)
//        viewModel.editedImageFiles(convertedFile, playlistName)
        progressDialog!!.dismiss()
    }

    override fun onFailure(error: Exception) {
        progressDialog!!.dismiss()
        Log.d(tagName, "onFailure " + error.message)

        toast(error.toString())
    }

    override fun onNotAvailable(error: Exception) {
        Log.d(tagName, "onNotAvailable() " + error.message)
        Log.v(tagName, "Exception: ${error.localizedMessage}")
    }

    override fun onFinish() {
        Log.d(tagName, "onFinish()")
    }

    private fun showBottomSheetDialogFragment(bottomSheetDialogFragment: BottomSheetDialogFragment) {
        val bundle = Bundle()
        bottomSheetDialogFragment.arguments = bundle
        fragmentManager?.let { bottomSheetDialogFragment.show(it, bottomSheetDialogFragment.tag) }
    }

}
