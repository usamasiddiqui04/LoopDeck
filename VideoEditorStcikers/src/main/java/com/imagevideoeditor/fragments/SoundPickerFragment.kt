package com.imagevideoeditor.fragments

import android.app.ProgressDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.PreviewPhotoActivity
import com.imagevideoeditor.PreviewVideoActivity
import com.imagevideoeditor.R
import com.imagevideoeditor.SoundListner
import com.imagevideoeditor.soundpicker.SongAdaptor
import com.imagevideoeditor.soundpicker.Songinfo
import com.obs.marveleditor.fragments.OptiBaseCreatorDialogFragment
import com.obs.marveleditor.interfaces.OptiDialogueHelper
import com.obs.marveleditor.interfaces.OptiFFMpegCallback
import kotlinx.android.synthetic.main.fragment_sound_picker.*
import kotlinx.android.synthetic.main.soundpickerlayout.*
import kotlinx.android.synthetic.main.soundpickerlayout.view.*
import kotlinx.android.synthetic.main.soundpickerlayout.view.play
import java.io.File


class SoundPickerFragment : BottomSheetDialogFragment() {


    var listSongs = ArrayList<Songinfo>()
    var songAdaptor: SongAdaptor? = null
    private var progressDialog: ProgressDialog? = null
    var videofile: File? = null
    var videoDuration: Long? = null
    var mediaPlayer: MediaPlayer? = null
    var previewVideoActivity: PreviewVideoActivity? = null
    var player: SimpleExoPlayer? = null


    companion object {
        fun newInstance() = SoundPickerFragment
    }

    private val onItemClickListener: (View, RecyclerView.ViewHolder, Songinfo) -> Unit =
        { itemView, viewHolder, songinfo ->

            AddMusicFragment.newInstance().apply {
                setaudiofilepath(File(songinfo.SongUrl!!), videofile!!, videoDuration!!)
            }.show(fragmentManager, "AddMusicFragment")
        }
//    private val playonItemClickListener: (View, RecyclerView.ViewHolder, Songinfo) -> Unit =
//        { itemView, viewHolder, songinfo ->
//
//            mediaPlayer!!.setDataSource(songinfo.SongUrl)
//            itemView.play.visibility = View.GONE
//            itemView.pause.visibility = View.VISIBLE
//            mediaPlayer!!.prepare()
//            mediaPlayer!!.start()
//        }
//    private val pauseonItemClickListener: (View, RecyclerView.ViewHolder, Songinfo) -> Unit =
//        { itemView, viewHolder, songinfo ->
//
//            mediaPlayer!!.setDataSource(songinfo.SongUrl)
//            itemView.play.visibility = View.VISIBLE
//            itemView.pause.visibility = View.GONE
//            mediaPlayer!!.stop()
//        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sound_picker, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSongs()
        mediaPlayer = MediaPlayer()
        progressDialog = ProgressDialog(requireContext())

        previewVideoActivity = PreviewVideoActivity()


        songAdaptor = SongAdaptor(listSongs, requireContext(), onItemClickListener)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = songAdaptor


        Log.e("List of songs: ", listSongs.toString())
        Toast.makeText(requireContext(), listSongs.size.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun loadSongs() {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!=0"

        val resultset = context!!.contentResolver.query(uri, null, selection, null, null)
        if (resultset != null) {
            while (resultset.moveToNext()) {
                val songurl =
                    resultset.getString(resultset.getColumnIndex(MediaStore.Audio.Media.DATA))
                val author =
                    resultset.getString(resultset.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                val title =
                    resultset.getString(resultset.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
                val duration =
                    resultset.getString(resultset.getColumnIndex(MediaStore.Audio.Media.DURATION))

                listSongs.add(Songinfo(title, author, songurl, duration))
            }
        }
    }

    fun setFilePath(file: File) {
        videofile = file
    }

    fun setMediaPlayer(player: SimpleExoPlayer) {
        this.player = player
    }

    fun setDuartion(timeInMillis: Long) {
        videoDuration = timeInMillis
    }


}