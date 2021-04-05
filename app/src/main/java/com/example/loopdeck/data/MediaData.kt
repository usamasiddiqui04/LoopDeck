package com.example.loopdeck.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.loopdeck.utils.extensions.getMediaType
import com.example.loopdeck.gallery.model.GalleryData
import java.util.*

@Entity(tableName = "MediaFileTable")
data class MediaData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val filePath: String,
    val name: String,
    val extension: String? = null,
    var sequence: Int,
    val mediaType: String,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
    val playListName: String? = null //Foriegn Lkey,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readSerializable() as Date?,
        parcel.readSerializable() as Date?,
        parcel.readString()
    ) {
    }

    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeInt(id)
        dest.writeString(filePath)
        dest.writeString(name)
        dest.writeString(extension)
        dest.writeInt(sequence)
        dest.writeString(mediaType)
        dest.writeSerializable(createdAt)
        dest.writeSerializable(modifiedAt)
        dest.writeString(playListName)
    }

    companion object CREATOR : Parcelable.Creator<MediaData> {
        override fun createFromParcel(parcel: Parcel): MediaData {
            return MediaData(parcel)
        }

        override fun newArray(size: Int): Array<MediaData?> {
            return arrayOfNulls(size)
        }
    }
}

object MediaType {
    val IMAGE = "image"
    val VIDEO = "video"
    val PLAYLIST = "playlist"
}


fun GalleryData.toMediaData() {
    MediaData(
        id = 0,
        filePath = this.photoUri,
        name = this.name,
        extension = file.extension,
        sequence = 0,
        mediaType = file.getMediaType()
    )
}