package ru.fox.media.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.fox.media.room.Album
import ru.fox.media.room.Song
import ru.fox.media.room.SongDao
import ru.fox.media.room.SongEntity
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit


class Repository(private val dao: SongDao) {
    private val type = TypeToken.getParameterized(List::class.java, Song::class.java).type
    private val songBaseUrl =
        "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"
    private val albumLink =
        "https://github.com/netology-code/andad-homeworks/raw/master/09_multimedia/data/album.json"
    private val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).build()

    //  private val jsonType = "application/json".toMediaType()
    var album: Album? = null

    fun loadAlbum() {
        //= Album(0,"empty","empty","empty","empty","empty",emptyList())
        val albumRequest = Request.Builder().url(albumLink).build()


        client.newCall(albumRequest)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (!response.isSuccessful) {
                        throw IOException(
                            "Запрос к серверу не был успешен:" +
                                    " ${response.code} ${response.message}"
                        )
                    } else try {
                        val body =
                            response.body?.string() ?: throw RuntimeException("body is null")
                        //Парсинг json файла в Java объект типа Album
                        album = Gson().fromJson(body, Album::class.java)
                        //Скачивание файлов из album
                        album?.tracks?.forEach { track ->
                            val songsRequest =
                                Request.Builder().url(songBaseUrl + track.file).build()
                            client.newCall(songsRequest)
                                .enqueue(object : Callback {
                                    override fun onResponse(call: Call, response: Response) {
                                        if (!response.isSuccessful) {
                                            throw IOException(
                                                "Запрос к серверу не был успешен:" +
                                                        " ${response.code} ${response.message}"
                                            )
                                        } else try {
                                            val downloadedSong = response.body?.bytes()

//                                            val sng =
                                                //File("songs/1.mp3")

                                           // File(filesDir,"songs").mkdir()
//                                            if (downloadedSong != null) {
//                                                sng.writeBytes(downloadedSong)
//                                            }
                                            val fos = FileOutputStream("files/songs/1.mp3")
                                            fos.write(downloadedSong)
                                            fos.close()

                                            println(downloadedSong)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                    override fun onFailure(call: Call, e: IOException) {
                                        e.printStackTrace()
                                    }
                                })
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }


                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }
            })

    }


    fun getAll(): LiveData<List<Song>> = dao.getAll().map { list ->
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