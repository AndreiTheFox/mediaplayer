package ru.fox.media.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SongEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val title: String,
    val favourite: Boolean = false,
    val url: String,
    val playing: Boolean,
)

 {
    fun toDto() = Song (id, author,title,favourite, url, playing)
    companion object {
        fun fromDto (dto: Song) =
            SongEntity(dto.id, dto.author, dto.title, dto.favourite, dto.url, dto.playing)
    }
}
