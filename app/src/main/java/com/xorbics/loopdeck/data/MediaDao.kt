package com.xorbics.loopdeck.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.xorbics.loopdeck.data.dao.BaseDAO


@Dao
interface MediaDao : BaseDAO<MediaData, PublishData> {

    @Query("SELECT * FROM MediaFileTable ORDER BY id ASC")
    fun findAll(): LiveData<List<MediaData>>

    @Query("SELECT * FROM MediaFileTable WHERE playListName is null ORDER BY modifiedAt ASC ")
    fun findRecents(): LiveData<List<MediaData>>

    @Query("SELECT * FROM MediaFileTable WHERE mediaType = 'playlist' ORDER BY modifiedAt ASC ")
    fun findAllPlaylist(): LiveData<List<MediaData>>


    @Query("SELECT * FROM PublishTable ORDER BY modifiedAt ASC ")
    fun findPublish(): LiveData<List<PublishData>>

    @Query("SELECT * FROM MediaFileTable WHERE playListName is :playlistName ORDER BY modifiedAt ASC ")
    fun findByPlaylist(playlistName: String): List<MediaData>

    @Query("SELECT * FROM MediaFileTable WHERE playListName is :playlistName ORDER BY sequence ASC ")
    fun findByPlaylistLiveData(playlistName: String): LiveData<List<MediaData>>

    @Query("SELECT filePath FROM MediaFileTable WHERE playListName is :playlistName AND sequence is 1")
    fun findByPlaylistImage(playlistName: String): LiveData<String>

    @Query("DELETE  FROM MediaFileTable WHERE id is :mediaDataId")
    fun deleteById(mediaDataId: String)

    @Query("DELETE FROM MediaFileTable WHERE playListName is :playlistName")
    fun deletePlaylist(playlistName: String)


}