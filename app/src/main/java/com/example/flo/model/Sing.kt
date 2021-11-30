package com.example.flo.model

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

data class Sing(val singer: String?, val album: String?, val title: String?, val imageUrl: String?, val file: String?, val lyrics: String?) {

    fun setImageByUrl(context: Context, imageView: ImageView) {
        imageUrl?.let {
            Glide.with(context)
                .load(it)
                .into(imageView)
        }
    }
}
