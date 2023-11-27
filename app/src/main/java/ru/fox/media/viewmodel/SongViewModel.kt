package ru.fox.media.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import ru.fox.media.repository.Repository
import ru.fox.media.room.Song
import ru.fox.media.room.SongDb

class SongViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: Repository =
        Repository(SongDb.getInstance(context = application).postDao())
    val data = repository.getAll()
    fun favouriteById(id: Long) = repository.favouriteById(id)
    fun getAlbum() = repository.loadAlbum()
    var newId : Long = 0L
    fun save() {
        val song = Song(
            id = newId,
            author = "Linking Park",
            title = " Song",
            favourite = false,
            url = "bla bla bla",
            playing = false,
        )
        repository.save(song)
        newId += 1L
    }

    fun songPlaying(song :Song, playing: Boolean)= repository.songPlaying(song, playing)
}