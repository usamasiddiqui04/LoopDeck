package com.example.loopdeck.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MediaData::class] , version = 1 , exportSchema = false)
abstract class MediaDatabase : RoomDatabase() {

    abstract fun mediaDao() : MediaDao

    companion object {
        @Volatile
        private var INSTANCE: MediaDatabase? = null

        fun getDatabase (context: Context) : MediaDatabase {
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