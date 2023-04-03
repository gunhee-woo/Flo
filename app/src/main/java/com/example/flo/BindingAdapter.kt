package com.example.flo

import android.widget.ImageView
import com.bumptech.glide.Glide

object BindingAdapter {

    @JvmStatic
    @androidx.databinding.BindingAdapter("albumCover")
    fun bindLoadImage(view: ImageView, url: String) {
        Glide.with(view.context)
            .load(url)
            .fitCenter()
            .into(view)
    }
}