package ru.fox.media.activity

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.fox.media.adapter.OnInteractionListener
import ru.fox.media.adapter.SongAdapter
import ru.fox.media.databinding.AppActivityBinding
import ru.fox.media.room.Song
import ru.fox.media.viewmodel.SongViewModel
import java.io.File


class AppActivity : AppCompatActivity() {

    private val songBaseUrl =
        "https://raw.githubusercontent.com/netology-code/andad-homeworks/master/09_multimedia/data/"

    var mediaPlayer: MediaPlayer = MediaPlayer()
    private var mediaRetriever = MediaMetadataRetriever()


    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: SongViewModel by viewModels()
        val binding = AppActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        viewModel.getAlbum()
        //Seekbar
        val seekbar = binding.seekbar
        val songs = viewModel.data
        var id = 0

        //Создание каталога для песен
        val folder = File(filesDir, "songs").mkdir()

        //Адаптер списка песен
        val adapter = SongAdapter(object : OnInteractionListener {
            override fun onPlaySongClick(song: Song) {

                //Нажали кнопку плей в листе

                //Если последняя песня, то берем первую песню из списка
                if (id == viewModel.data.value?.size) {
                    id = 0
                }


                //Иначе
                println(song)
                println(viewModel.data.value?.get(song.id.toInt())?.playing)
                //Если медиа плеер уже играет, то остановим
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.stop()

                }


                //Если для воспроизведения выбрана другая песня, не важно играет медиаплеер при этом или нет
                //  TODO()         if (song != ) {
                //Скидываем прогресс в 0 при новой песне
                seekbar.progress = 0
                //Скидываем медиаплеер в начало воспроизведения
                mediaPlayer.reset()
                //         }


                //Put Song in Mediaplayer and change metadata in player

                val songUrl = songBaseUrl + song.url
                mediaPlayer.setDataSource(songUrl)
                mediaPlayer.prepareAsync()

                //Заполнение элементов интерфейса из файла песни
                mediaRetriever.setDataSource(songUrl)
                val band =
                    mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                val songTitle =
                    mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                val albumName =
                    mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                val year = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
                val genre =
                    mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
                binding.bandPlaying.text = band
                binding.songPlaying.text = songTitle
                binding.band.text = band
                binding.albumName.text = albumName
                binding.albumYear.text = year
                binding.albumGenre.text = genre

                mediaPlayer.setOnPreparedListener {
                    //Записываем длительность песни в трекбар
                    seekbar.max = mediaPlayer.duration
                    it.start()
                }
            }

            //Mark as favourite in repository
            override fun onFavourite(song: Song) {
                viewModel.favouriteById(song.id)
            }
        })

        //Привязка адаптера к RecyclerView
        binding.list.adapter = adapter

        //Получим Список песен из LiveData у класса viewModel
        viewModel.data.observe(this) { songs ->
            adapter.submitList(songs)
        }

        //Load song in player
        binding.playCurrentSongInMediaPlayer.setOnClickListener {
            when {
//                mediaPlayer.currentPosition == 0 && !mediaPlayer.isPlaying -> {
//                    loadSongInPlayer()
//                }
                !mediaPlayer.isPlaying -> {
                    mediaPlayer.start()
                }

                else -> {
                    mediaPlayer.pause()
                }
            }
        }

        mediaPlayer.setOnCompletionListener {
            binding.playCurrentSongInMediaPlayer.isChecked = false
            it.reset()
        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, pos: Int, changed: Boolean) {
                if (changed) {
                    mediaPlayer.seekTo(pos)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })

//        //Движение seekbar во время проигрывания песни
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    seekbar.progress = mediaPlayer.currentPosition
                    handler.postDelayed(this, 1000)
                } catch (e: Exception) {
                    println(e.stackTrace)
                }
            }
        }, 0)
    } //Конец onCreate

    override fun onDestroy() {
        super.onDestroy()
        //Освободить ресурсы плеера при закрытии
        mediaPlayer.release()
    }


}//Конец Activity