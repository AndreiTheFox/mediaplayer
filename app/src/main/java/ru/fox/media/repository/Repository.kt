package ru.fox.media.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.fox.media.room.Song
import ru.fox.media.room.SongDao
import ru.fox.media.room.SongEntity

class Repository(private val dao: SongDao) {
    fun getAll(): LiveData<List<Song>> = dao.getAll().map{ list ->
        list.map {
            it.toDto()
        }
    }

    fun favouriteById(id: Long) {
        dao.favouriteById(id)
    }
    fun save(song: Song) {
        dao.insert(SongEntity.fromDto(song))
        println("добавлена запись с ID: ${song.id}")
    }
}