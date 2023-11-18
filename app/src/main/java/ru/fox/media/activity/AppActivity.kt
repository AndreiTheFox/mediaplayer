package ru.fox.media.activity

import android.os.Bundle
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
    private val mediaObserver = MediaLifecycleObserver()
    private var mediaPlayer = mediaObserver.player

    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: SongViewModel by viewModels()
        val binding = AppActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        //Подписка на жизненный цикл активити
        lifecycle.addObserver(observer = mediaObserver)

        val mySongs = listOf(R.raw.faint, R.raw.breaking_the_habit)
        var id = 0
        //TESTTTTTTTTTTTTTTTT
//        val currentSong = MediaPlayer.create(this, mySongs[id])

        //Адаптер списка песен
        val adapter = SongAdapter(object : OnInteractionListener {
            override fun onSongClick(song: Song) {
                //Play music if stop. Stop if play.
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

        //Кнопки управления плеером

        //Load song in player
        fun loadSongInPlayer () {
            if (id == mySongs.size) {
                id = 0
            }
            mediaPlayer.stop()
            mediaPlayer.reset()
            val song = resources.openRawResourceFd(mySongs[id])
            mediaPlayer.setDataSource(song.fileDescriptor, song.startOffset, song.length)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                seekbar.progress = 0
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
            seekbar.progress = 0
                it.reset()
        }


        binding.fab.setOnClickListener {
            id+=1
            binding.playButton.isChecked = false
            loadSongInPlayer()
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
}//Конец Activity