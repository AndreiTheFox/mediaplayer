package ru.fox.media.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.fox.media.databinding.CardSongBinding
import ru.fox.media.room.Song

interface OnInteractionListener {
    fun onFavourite(song: Song){}
    fun onPlaySongClick(song: Song){}
}
class SongAdapter(private val onInteractionListener: OnInteractionListener) :
    ListAdapter<Song, SongViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val binding = CardSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = getItem(position)
        holder.bind(song)
    }
}

class SongViewHolder(
    private val binding: CardSongBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(song: Song) {
        binding.apply {
            author.text = song.author
            songTitle.text = song.title
            favouriteButton.isChecked = song.favourite
            favouriteButton.setOnClickListener {
                onInteractionListener.onFavourite(song)
            }
            playButton.setOnClickListener {
                onInteractionListener.onPlaySongClick(song)
            }
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem == newItem
    }

}