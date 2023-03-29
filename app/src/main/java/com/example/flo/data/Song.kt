package com.example.flo.data

import com.google.gson.annotations.SerializedName

data class Song(
    @field:SerializedName("singer") val singer: String,
    @field:SerializedName("album") val album: String,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("duration") val duration: Int,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("file") val file: String,
    @field:SerializedName("lyrics") val lyrics: String
)
