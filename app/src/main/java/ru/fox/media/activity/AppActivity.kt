package ru.fox.media.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.fox.media.adapter.OnInteractionListener
import ru.fox.media.adapter.SongAdapter
import ru.fox.media.databinding.AppActivityBinding
import ru.fox.media.room.Song
import ru.fox.media.viewmodel.SongViewModel

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val viewModel: SongViewModel by viewModels()
        val binding = AppActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)

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
        binding.fab.setOnClickListener {
            viewModel.save()
        }

        //Привязка адаптера к RecyclerView
        binding.list.adapter = adapter

        //Список песен из LiveData у viewModel
        viewModel.data.observe(this) { songs ->
            adapter.submitList(songs)
        }

        //Кнопки управления плеером



    } //Конец onCreate
}//Конец Activity