package ru.fox.media.room

data class Song(
    val id: Long,
    val author: String,
    val title: String,
    val favourite: Boolean = false,
    val url: String,
)
