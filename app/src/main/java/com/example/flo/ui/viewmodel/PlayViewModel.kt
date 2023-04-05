package com.example.flo.ui.viewmodel

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flo.data.Song
import com.example.flo.network.FloService
import kotlinx.coroutines.launch
import okhttp3.internal.format
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PlayViewModel: ViewModel() {

    private val _song = MutableLiveData<Song>()
    val song: LiveData<Song> get() = _song
    val mediaPlayer = MediaPlayer()

    val isPlaying = ObservableBoolean(false)

    private val _uiEvent = MutableLiveData<Pair<Int, Any>>()
    val uiEvent: LiveData<Pair<Int, Any>> get() = _uiEvent

    companion object {
        const val NOTHING = 0
        const val NOTIFY = 1
        const val PREPARED = 2
        const val PROGRESS = 3
    }

    init {
        getSongInfo()
    }

    private fun getSongInfo() {
        viewModelScope.launch {
            val result = FloService.create().getSongInfo()
            Timber.d(result.body().toString())
            if(result.body() != null) {
                result.body()?.let {
                    prepareMediaPlayer(it)
                    _song.postValue(it)
                }
            } else {
                _uiEvent.postValue(NOTIFY to "NotRespond")
            }
        }
    }

    private fun prepareMediaPlayer(song: Song) {
        mediaPlayer.apply {
            setAudioAttributes(
                AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            )
            try {
                setDataSource(song.file)
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.d(e.toString())
            }
            setVolume(0.5f, 0.5f)
            isLooping = false
            setOnPreparedListener {
                _uiEvent.value = PREPARED to duration
            }
            setOnCompletionListener {
                stop()
                release()
            }
            prepareAsync()
        }
    }

    fun getTimeString(duration: Int, isVisibleMillis: Boolean = false): String {
        val hours = TimeUnit.MILLISECONDS.toHours(duration.toLong())
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration.toLong())
        val milliseconds = TimeUnit.MILLISECONDS.toMillis(duration.toLong())
        return if (hours > 0) {
            format(
                "%s%02d:%02d:%02d",
                "",
                hours,
                minutes - TimeUnit.HOURS.toMinutes(hours),
                seconds - TimeUnit.MINUTES.toSeconds(minutes)
            )
        } else if(isVisibleMillis) {
            format(
                "%s%02d:%02d:%03d",
                "",
                minutes,
                seconds - TimeUnit.MINUTES.toSeconds(minutes),
                milliseconds - TimeUnit.SECONDS.toMillis(seconds)
            )
        } else {
            format(
                "%s%02d:%02d",
                "",
                minutes,
                seconds - TimeUnit.MINUTES.toSeconds(minutes)
            )
        }
    }

    inner class PlayThread(): Thread() {
        override fun run() {
            super.run()
            var current = mediaPlayer.currentPosition
            val total = mediaPlayer.duration
            while(mediaPlayer.isPlaying && current < total) {
                try {
                    current = mediaPlayer.currentPosition
                    Timber.d("currentPos : $current")
                    _uiEvent.postValue(PROGRESS to current)
//                    Handler(Looper.getMainLooper()).post {
//                        binding.timePgTv.text = getTimeString(current)
//                        binding.singSb.progress = current
//                    }
                    sleep(500)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.d(e.toString())
                    return
                }
            }
            _uiEvent.postValue(PROGRESS to -1)
        }
    }
}