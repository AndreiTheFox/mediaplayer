package ru.fox.media.activity

import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class MediaLifecycleObserver : LifecycleEventObserver {
    var player: MediaPlayer = MediaPlayer()
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> player.currentPosition
            Lifecycle.Event.ON_PAUSE -> player.pause()
            Lifecycle.Event.ON_STOP -> {
//                player.release()
                player.stop()
//                player.reset()
            }
            Lifecycle.Event.ON_DESTROY -> source.lifecycle.removeObserver(this)

            else -> Unit
        }
    }
}