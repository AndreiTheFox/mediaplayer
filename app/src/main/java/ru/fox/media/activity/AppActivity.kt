package ru.fox.media.activity

import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.fox.media.R
import ru.fox.media.adapter.OnInteractionListener
import ru.fox.media.adapter.SongAdapter
import ru.fox.media.databinding.AppActivityBinding
import ru.fox.media.room.Song
import ru.fox.media.viewmodel.SongViewModel

class AppActivity : AppCompatActivity() {
    var mediaPlayer: MediaPlayer = MediaPlayer()
    private var mediaRetriever = MediaMetadataRetriever()

    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: SongViewModel by viewModels()
        val binding = AppActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        val mySongs = listOf(R.raw.faint, R.raw.breaking_the_habit)
        var id = 0

        //Адаптер списка песен
        val adapter = SongAdapter(object : OnInteractionListener {
            override fun onSongClick(song: Song) {
                //Put Song in Mediaplayer and change metadata in player
            }

            //Mark as favourite in repository
            override fun onFavourite(song: Song) {
                viewModel.favouriteById(song.id)
            }
        })

        //Тест сохранения в БД
//        binding.fab.setOnClickListener {
//            viewModel.save()
//        }

        //Привязка адаптера к RecyclerView
        binding.list.adapter = adapter

        //Получим Список песен из LiveData у класса viewModel
        viewModel.data.observe(this) { songs ->
            adapter.submitList(songs)
        }

        //Seekbar
        val seekbar = binding.seekbar

        //Load song in player
        fun loadSongInPlayer() {
            //Скидываем прогресс в 0 при новой песне
            seekbar.progress = 0
            //Если последняя песня, то берем первую песню из списка
            if (id == mySongs.size) {
                id = 0
            }
            mediaPlayer.stop()
            mediaPlayer.reset()

            //Загрузка песни в плеер
            val song = resources.openRawResourceFd(mySongs[id])

            mediaPlayer.setDataSource(song.fileDescriptor, song.startOffset, song.length)
            mediaPlayer.prepareAsync()

            //Заполнение элементов интерфейса из файла песни
            mediaRetriever.setDataSource(song.fileDescriptor, song.startOffset, song.length)
            val band =
                mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
            val songTitle =
                mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
            val albumName =
                mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
            val year = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_YEAR)
            val genre = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)
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

        binding.playButton.setOnClickListener {
            when {
                mediaPlayer.currentPosition == 0 && !mediaPlayer.isPlaying -> {
                    loadSongInPlayer()
                }

                !mediaPlayer.isPlaying -> {
                    mediaPlayer.start()
                }

                else -> {
                    mediaPlayer.pause()
                }
            }
        }

        mediaPlayer.setOnCompletionListener {
            binding.playButton.isChecked = false
            it.reset()
        }

        binding.fab.setOnClickListener {
            id += 1
            binding.playButton.isChecked = false
            loadSongInPlayer()
        }

//        if(mediaPlayer.isPlaying){
//             seekbar.progress = mediaPlayer.duration
//        }


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


        //Движение seekbar во время проигрывания песни
        @Suppress("DEPRECATION")
        Handler().postDelayed(object : Runnable{
            override fun run() {
                try {
                    seekbar.progress = mediaPlayer.currentPosition
                    Handler().postDelayed(this,1000)
                }
                catch (e:Exception){
                    println(e.stackTrace)
                }
            }

        }, 0)


        /*
                findViewById<Button>(R.id.play).setOnClickListener {
                    mediaObserver.apply {
                        player?.setDataSource(
                            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                        )
                    }.play()
                }
        */
    } //Конец onCreate

    override fun onDestroy() {
        super.onDestroy()
        //Освободить ресурсы плеера при закрытии
        mediaPlayer.release()
    }

}//Конец Activity