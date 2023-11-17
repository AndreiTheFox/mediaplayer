package ru.fox.media.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [SongEntity::class], version = 1)
abstract class SongDb: RoomDatabase() {
    abstract fun postDao(): SongDao
    companion object {
        @Volatile
        private var instance: SongDb? = null
        fun getInstance(context: Context): SongDb {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, SongDb::class.java, "Songs.db")
                .allowMainThreadQueries()
                .build()
    }
}