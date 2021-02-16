package com.imagevideoeditor.fragments

import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.imagevideoeditor.R
import com.imagevideoeditor.soundpicker.SongAdaptor
import com.imagevideoeditor.soundpicker.Songinfo
import kotlinx.android.synthetic.main.fragment_sound_picker.*


class SoundPickerFragment : BottomSheetDialogFragment() {


    var listSongs = ArrayList<Songinfo>()
    var mediaPlayer: MediaPlayer? = null
    var songAdaptor: SongAdaptor? = null
    var linearLayoutManager: LinearLayoutManager? = null


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

        songAdaptor = SongAdaptor(listSongs, requireContext())
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
}