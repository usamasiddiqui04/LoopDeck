package com.example.loopdeck.ui.collection.recents

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loopdeck.DragData
import com.example.loopdeck.R
import com.example.loopdeck.data.MediaData
import com.example.loopdeck.data.MediaType
import com.example.loopdeck.drawer.AdvanceDrawerLayout
import com.example.loopdeck.onedrive.ApiExplorer
import com.example.loopdeck.onedrive.ItemFragment
import com.example.loopdeck.ui.adapters.MediaAdaptor
import com.example.loopdeck.ui.collection.CollectionViewModel
import com.example.loopdeck.ui.collection.playlist.PlaylistFragment
import com.example.loopdeck.utils.extensions.activityViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.imagevideoeditor.PreviewVideoActivity
import com.loopdeck.photoeditor.EditImageActivity
import com.example.loopdeck.gallery.model.GalleryData
import com.example.loopdeck.gallery.view.PickerActivity
import com.imagevideoeditor.PreviewPhotoActivity
import com.xorbix.loopdeck.cameraapp.BitmapUtils
import kotlinx.android.synthetic.main.custom_layout.view.*
import kotlinx.android.synthetic.main.dailogbox.view.*
import kotlinx.android.synthetic.main.fragment_recents.*
import kotlinx.android.synthetic.main.item_recent_folder_list.view.*
import kotlinx.android.synthetic.main.item_recent_folder_list.view.selectitem
import kotlinx.android.synthetic.main.item_recent_list_images.view.*
import kotlinx.android.synthetic.main.item_recent_video_lists.view.*
import java.io.File
import java.util.*

class RecentsFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var drawer: AdvanceDrawerLayout? = null
    private var selectedList = ArrayList<MediaData>()
    var string: MediaData? = null
    var multiSelection: Boolean = false

    companion object {
        fun newInstance() = RecentsFragment()
        private const val REQUEST_RESULT_CODE = 100
    }

    private lateinit var viewModel: CollectionViewModel

    private val mediaAdapter by lazy {
        MediaAdaptor(mList = mutableListOf(), onItemClickListener, onItemLongClickListener)
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
        string = list.get(viewHolder.adapterPosition)
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
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }
        (activity as AppCompatActivity?)!!.setSupportActionBar(toolbar)

//        ViewCompat.setLayoutDirection(drawer_layout!!, ViewCompat.LAYOUT_DIRECTION_RTL)
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout!!.addDrawerListener(toggle)

        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        drawer_layout!!.setViewScale(GravityCompat.START, 0.9f)
        drawer_layout!!.setRadius(GravityCompat.START, 35f)
        drawer_layout!!.setViewElevation(GravityCompat.START, 20f)
        recyclerview?.adapter = mediaAdapter
        recyclerview?.layoutManager = GridLayoutManager(requireContext(), 3)

        btnCreate.setOnClickListener {
            savePlaylistNameDialog(requireContext()) {
                viewModel.createPlaylist(it)
            }
        }

        btnDelete.setOnClickListener {
            for (list in selectedList) {
                viewModel.delete(list)
            }
            selectedList.clear()
            multiSelection = false
            bottomLayout.visibility = View.GONE

        }

        addmedia.setOnClickListener {
            val i = Intent(requireActivity(), PickerActivity::class.java)
            i.putExtra("IMAGES_LIMIT", 100)
            i.putExtra("VIDEOS_LIMIT", 100)
            i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }

        btnGallery.setOnClickListener {

            val i = Intent(requireActivity(), PickerActivity::class.java)
            i.putExtra("IMAGES_LIMIT", 100)
            i.putExtra("VIDEOS_LIMIT", 100)
            i.putExtra("REQUEST_RESULT_CODE", REQUEST_RESULT_CODE)
            startActivityForResult(i, REQUEST_RESULT_CODE)
        }
        initContainer()
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
        savePlaylistNameDialog(context) {
            viewModel.createPlaylist(it)
            viewModel.addMediaFiles(mediaList, it.name)
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

        viewModel.recentsMediaLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { list ->
                if (list.isEmpty()) {
                    recyclerview.visibility = View.INVISIBLE
                    no_mediafile.visibility = View.VISIBLE
                    addmedia.visibility = View.INVISIBLE
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        drawer_layout!!.closeDrawer(GravityCompat.START)
        return true
    }


}
