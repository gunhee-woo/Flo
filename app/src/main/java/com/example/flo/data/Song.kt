package com.example.flo.data

import com.google.gson.annotations.SerializedName
import timber.log.Timber

data class Song(
    @field:SerializedName("singer") val singer: String,
    @field:SerializedName("album") val album: String,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("duration") val duration: Int,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("file") val file: String,
    @field:SerializedName("lyrics") val lyrics: String
) {
    fun getLyricMap(): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        lyrics.split("\n").forEach {
            it.split("]").also { itt ->
                Timber.d("1 : ${itt[0].drop(1)}, 2 : ${itt[1]}")
                map[itt[0].drop(1)] = itt[1]
            }
        }
        return map
    }
}
