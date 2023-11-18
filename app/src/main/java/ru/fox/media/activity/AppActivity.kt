package ru.fox.media.activity

import android.media.MediaPlayer
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
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: SongViewModel by viewModels()
        val binding = AppActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

        val mySongs  = listOf(R.raw.faint, R.raw.breaking_the_habit)


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

        //Список песен из LiveData у viewModel
        viewModel.data.observe(this) { songs ->
            adapter.submitList(songs)
        }

        lifecycle.addObserver(mediaObserver)

        var id = 0

        //TESTTTTTTTTTTTTTTTT
        val currentSong = MediaPlayer.create(this, mySongs[id])


        //Seekbar
        val seekbar = binding.seekbar
        seekbar.progress = 0
        seekbar.max = currentSong.duration

        //Кнопки управления плеером

        binding.playButton.setOnClickListener {
            if (!currentSong.isPlaying) {
                currentSong.start()
            } else {
                currentSong.pause()
            }
        }


        binding.fab.setOnClickListener {
            id+=1
            currentSong.selectTrack(id)
        }

        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, pos: Int, changed: Boolean) {
                if (changed) {
                    currentSong.seekTo(pos)
                }
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        currentSong.setOnCompletionListener {
            binding.playButton.isChecked = false
            seekbar.progress = 0
            it.release()
        }


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