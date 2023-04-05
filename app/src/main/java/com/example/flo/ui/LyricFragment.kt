package com.example.flo.ui

import androidx.fragment.app.activityViewModels
import com.example.flo.R
import com.example.flo.databinding.FragmentLyricBinding
import com.example.flo.ui.viewmodel.PlayViewModel

class LyricFragment: BaseFragment<FragmentLyricBinding>(R.layout.fragment_lyric) {
    private val viewModel: PlayViewModel by activityViewModels()

    override fun init() {
        binding.viewModel = viewModel
    }

}