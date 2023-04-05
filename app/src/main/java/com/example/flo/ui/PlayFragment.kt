package com.example.flo.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.SeekBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.flo.App
import com.example.flo.R
import com.example.flo.databinding.FragmentPlayBinding
import com.example.flo.ui.viewmodel.PlayViewModel
import timber.log.Timber


class PlayFragment: BaseFragment<FragmentPlayBinding>(R.layout.fragment_play) {
    private val viewModel: PlayViewModel by activityViewModels()

    override fun init() {
        Timber.d(App.Function())
        binding.viewModel = viewModel
        binding.titleTv.isSelected = true
        subscribeLiveData()
        setEvent()
    }

    private fun subscribeLiveData() {
        viewModel.song.observe(this) {
            Timber.d("song subscribe livedata")
            loadImage(it.image)
//            viewModel.mediaPlayer.apply {
//                setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
//                try {
//                    setDataSource(it.file)
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                    Timber.d(e.toString())
//                }
//                prepareAsync()
//            }
            it.getLyricsList().also { lyric ->
                binding.lyricTv.text = "${lyric[0].second} \n ${lyric[1].second}"
//                binding.lyricTv1.text = lyric[0].second
//                binding.lyricTv2.text = lyric[1].second
            }
        }
        viewModel.uiEvent.observe(this) {
            when(it.first) {
                PlayViewModel.NOTIFY -> {
                    Toast.makeText(context, it.second as String, Toast.LENGTH_SHORT).show()
                }
                PlayViewModel.PREPARED -> {
                    Timber.d("duration : ${it.second as Int}")
                    binding.timePgEndTv.text = viewModel.getTimeString(it.second as Int)
                    binding.singSb.max = it.second as Int
                }
                PlayViewModel.PROGRESS -> {
                    val current = it.second as Int
                    if(current != -1) {
                        Timber.d("current : $current, timeString : ${viewModel.getTimeString(current, true)}")
                        binding.timePgTv.text = viewModel.getTimeString(current)
                        binding.singSb.progress = current
                        setLyrics(current)
                    } else {
                        ix = 1
                    }
                }
            }
        }
    }

    private var ix = 1

    private fun setLyrics(cur: Int) {
        viewModel.song.value?.let { song ->
            val lyricList = song.getLyricsList()
            val startTime = lyricList[ix - 1].first
            val nextTime = lyricList[ix].first
            Timber.d("current : $cur, startTime : $startTime, nextTime : $nextTime, lyricList.size : ${lyricList.size}")
            if(cur in startTime until nextTime) {
                binding.lyricTv.text = getCurrentLyric(lyricList[ix - 1].second, lyricList[ix].second)
            }
            else if(cur >= nextTime) {
                ix++
                if(ix >= lyricList.size) {
                    binding.lyricTv.text = getCurrentLyric(lyricList[ix - 1].second, "")
                } else {
                    binding.lyricTv.text = getCurrentLyric(lyricList[ix - 1].second, lyricList[ix].second)
                }
            }
            Timber.d("lyric : ${binding.lyricTv.text}")
//            if(cur >= nextTime) ix++
//            binding.lyricTv.text = getCurrentLyric(lyricList[ix - 1].second, lyricList[ix].second)
        }
    }

    private fun getCurrentLyric(lyric1: String, lyric2: String) = SpannableString("$lyric1\n$lyric2").apply {
        setSpan(ForegroundColorSpan(Color.WHITE), 0, lyric1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun setEvent() {
        binding.playBtn.setOnClickListener {
            if(viewModel.mediaPlayer.isPlaying) {
                viewModel.mediaPlayer.pause()
            } else {
                playSong()
            }
            viewModel.isPlaying.set(viewModel.mediaPlayer.isPlaying)
        }
        binding.singSb.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(p0: SeekBar?, progress: Int, fromUser: Boolean) {
                Timber.d("${App.Function()}, progress : $progress, fromTouch : $fromUser")
                if(fromUser) {
                    viewModel.mediaPlayer.seekTo(progress)
                }
                binding.timePgTv.text = viewModel.getTimeString(progress)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                Timber.d(App.Function())

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                Timber.d(App.Function())
            }

        })
//        binding.lyricLly.setOnClickListener {
//            navigateToLyricFragment()
//        }
    }

    private fun playSong() {
        binding.timePgTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.teal_200))
        viewModel.mediaPlayer.start()
        viewModel.PlayThread().start()
    }

    private fun clearPlayer() {
        viewModel.mediaPlayer.stop()
        viewModel.mediaPlayer.release()
    }

    private fun loadImage(url: String) {
        Glide.with(binding.albumCoverIv)
            .load(url)
            .fitCenter()
            .into(binding.albumCoverIv)
    }

    private fun navigateToLyricFragment() {
        val action = PlayFragmentDirections.actionPlayFragmentToLyricFragment()
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearPlayer()
    }
}