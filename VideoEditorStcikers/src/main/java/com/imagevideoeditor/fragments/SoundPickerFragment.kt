package com.imagevideoeditor.fragments

import android.app.ProgressDialog
import android.content.DialogInterface
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.R
import com.imagevideoeditor.soundpicker.SongAdaptor
import com.imagevideoeditor.soundpicker.Songinfo
import kotlinx.android.synthetic.main.fragment_sound_picker.*
import java.io.File


class SoundPickerFragment : BottomSheetDialogFragment() {


    var listSongs = ArrayList<Songinfo>()
    var songFilterList = ArrayList<Songinfo>()
    var songAdaptor: SongAdaptor? = null
    private var progressDialog: ProgressDialog? = null
    var videofile: File? = null
    var imagefile: File? = null
    var videoDuration: Long? = null
    var list: List<Songinfo>? = null

    var mediaPlayer: MediaPlayer? = null
    var listener: SoundPickerListener? = null

    var selectForVideo = true
    private lateinit var addMusicListener: AddMusicFragment.AddMusicFragmentListener

    companion object {
        fun newInstance(
            soundPickerListener: SoundPickerListener,
            musicListener: AddMusicFragment.AddMusicFragmentListener,
            selectForVideo: Boolean = true
        ) = SoundPickerFragment().apply {
            listener = soundPickerListener
            addMusicListener = musicListener
            this.selectForVideo = selectForVideo

        }
    }

    private val onItemClickListener: (View, RecyclerView.ViewHolder, Songinfo) -> Unit =
        { itemView, viewHolder, songinfo ->

            AddMusicFragment.newInstance().apply {
                if (selectForVideo) {
                    setAudioVideoFilePaths(File(songinfo.SongUrl!!), videofile!!, videoDuration!!)
                } else {
                    setAudioImageFilePaths(File(songinfo.SongUrl!!), imagefile!!, videoDuration!!)
                }

                setAddMusicListener(addMusicListener)

            }.show(fragmentManager, "AddMusicFragment")
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sound_picker, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        list = ArrayList()
        loadSongs()
        progressDialog = ProgressDialog(requireContext())

        songAdaptor = SongAdaptor(
            listSongs,
            requireContext(),
            onItemClickListener,
            onPlayPressed,
            onPausePressed
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = songAdaptor


        Log.e("List of songs: ", listSongs.toString())
        Toast.makeText(requireContext(), listSongs.size.toString(), Toast.LENGTH_SHORT).show()

        stickersearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                songAdaptor!!.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
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
                songAdaptor?.notifyDataSetChanged()
            }
        }
    }

    fun setVideoFilePath(file: File) {
        videofile = file
    }

    fun setImageFilePath(file: File) {
        imagefile = file
    }


    fun setDuartion(timeInMillis: Long) {
        videoDuration = timeInMillis
    }

    val onPlayPressed: (Songinfo) -> Unit = {

        mediaPlayer?.stop()

        mediaPlayer = MediaPlayer()

        mediaPlayer?.apply {
            setDataSource(it.SongUrl)
            prepare()
            start()
        }
    }


    val onPausePressed: (Songinfo) -> Unit = {

        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer()
        mediaPlayer?.stop()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)

        listener?.onDismissSoundPicker()

    }

    interface SoundPickerListener {
        fun onDismissSoundPicker(): Unit
    }
}

