package com.example.flo.ui.viewmodel

import android.media.MediaPlayer
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flo.data.Song
import com.example.flo.network.FloService
import kotlinx.coroutines.launch
import timber.log.Timber

class PlayViewModel: ViewModel() {
    private val _song = MutableLiveData<Song>()
    val song: LiveData<Song> get() = _song

    val mediaPlayer = MediaPlayer()

    val isPlaying = ObservableBoolean(false)

    fun getSongInfo() {
        viewModelScope.launch {
            val result = FloService.create().getSongInfo()
            Timber.d(result.body().toString())
            if(result.isSuccessful)
                result.body()?.let { _song.postValue(it) }
        }
    }
}