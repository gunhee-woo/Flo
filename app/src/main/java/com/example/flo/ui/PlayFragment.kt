package com.example.flo.ui

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.flo.App
import com.example.flo.R
import com.example.flo.databinding.FragmentPlayBinding
import com.example.flo.ui.viewmodel.PlayViewModel
import okhttp3.internal.format
import timber.log.Timber
import java.util.concurrent.TimeUnit


class PlayFragment: BaseFragment<FragmentPlayBinding>(R.layout.fragment_play) {
    private val viewModel: PlayViewModel by viewModels()
    private var mediaPlayer: MediaPlayer = MediaPlayer()


    override fun init() {
        Timber.d(App.Function())
        binding.viewModel = viewModel
        viewModel.getSongInfo()
        binding.titleTv.isSelected = true
        subscribeLiveData()
        setEvent()
    }

    private fun subscribeLiveData() {
        viewModel.song.observe(this) {
            loadImage(it.image)
            mediaPlayer.apply {
                setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                try {
                    setDataSource(it.file)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.d(e.toString())
                }
                prepareAsync()
            }
        }
    }

    private fun setEvent() {
        binding.playBtn.setOnClickListener {
            if(mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                playSong()
            }
            viewModel.isPlaying.set(mediaPlayer.isPlaying)
        }
        binding.singSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                Timber.d("${App.Function()}, progress : $progress, fromTouch : $fromUser")
                if(fromUser) {
                    mediaPlayer.seekTo(progress)
                }
                binding.timePgTv.text = getTimeString(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Timber.d(App.Function())
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Timber.d(App.Function())
            }

        })
    }

    private fun playSong() {
        binding.singSb.max = mediaPlayer.duration
        binding.timePgEndTv.text = getTimeString(mediaPlayer.duration)
        binding.timePgTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
        mediaPlayer.setVolume(0.5f, 0.5f)
        mediaPlayer.isLooping = false
        mediaPlayer.start()
        PlayThread().start()
    }

    private fun clearPlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun loadImage(url: String) {
        Glide.with(binding.albumCoverIv)
            .load(url)
            .fitCenter()
            .into(binding.albumCoverIv)
    }

    private fun getTimeString(duration: Int, isVisibleMillis: Boolean = false): String {
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

    override fun onDestroyView() {
        super.onDestroyView()
        clearPlayer()
    }

    inner class PlayThread: Thread() {
        override fun run() {
            super.run()
            var current = mediaPlayer.currentPosition
            val total = mediaPlayer.duration
            while(mediaPlayer.isPlaying && current < total) {
                try {
                    current = mediaPlayer.currentPosition
                    Timber.d("currentPos : $current")
                    Handler(Looper.getMainLooper()).post {
                        binding.timePgTv.text = getTimeString(current)
                        binding.singSb.progress = current
                    }
                    sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.d(e.toString())
                    return
                }
            }
        }
    }
}