package com.example.loopdeck.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.loopdeck.data.converters.Converters

@Database(entities = [MediaData::class, PublishData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MediaDatabase : RoomDatabase() {

    abstract fun mediaDao(): MediaDao

    companion object {
        @Volatile
        private var INSTANCE: MediaDatabase? = null

        fun getDatabase(context: Context): MediaDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MediaDatabase::class.java,
                    "loopdeckMedia_Database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}