package com.xorbix.loopdeck.data

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "PublishTable")
class PublishData(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val filePath: String,
    val name: String,
    val extension: String? = null,
    val mediaType: String,
    val createdAt: Date? = null,
    val modifiedAt: Date? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString(),
        parcel.readString().toString(),
        parcel.readSerializable() as Date?,
        parcel.readSerializable() as Date?,
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
        dest.writeString(mediaType)
        dest.writeSerializable(createdAt)
        dest.writeSerializable(modifiedAt)
    }

    companion object CREATOR : Parcelable.Creator<PublishData> {
        override fun createFromParcel(parcel: Parcel): PublishData {
            return PublishData(parcel)
        }

        override fun newArray(size: Int): Array<PublishData?> {
            return arrayOfNulls(size)
        }
    }
}


