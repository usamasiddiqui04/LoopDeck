package com.example.loopdeck.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.loopdeck.data.dao.BaseDAO


@Dao
interface MediaDao : BaseDAO<MediaData> {

    @Query("SELECT * FROM MediaFileTable ORDER BY id ASC")
    fun findAll(): LiveData<List<MediaData>>


    @Query("SELECT * FROM MediaFileTable WHERE playListName is null ORDER BY modifiedAt ASC ")
    fun findRecents(): LiveData<List<MediaData>>


    @Query("SELECT * FROM MediaFileTable WHERE playListName is :playlistName ORDER BY modifiedAt ASC ")
    fun findByPlaylist(playlistName: String): List<MediaData>

    @Query("SELECT * FROM MediaFileTable WHERE playListName is :playlistName ORDER BY sequence ASC ")
    fun findByPlaylistLiveData(playlistName: String): LiveData<List<MediaData>>

    @Query("DELETE  FROM MediaFileTable WHERE id is :mediaDataId")
    fun deleteById(mediaDataId: String)

    @Query("DELETE FROM MediaFileTable WHERE playListName is :playlistName")
    fun deletePlaylist(playlistName: String)


}