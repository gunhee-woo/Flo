package com.example.flo.data

import com.google.gson.annotations.SerializedName
import timber.log.Timber
import java.sql.Timestamp

data class Song(
    @field:SerializedName("singer") val singer: String,
    @field:SerializedName("album") val album: String,
    @field:SerializedName("title") val title: String,
    @field:SerializedName("duration") val duration: Int,
    @field:SerializedName("image") val image: String,
    @field:SerializedName("file") val file: String,
    @field:SerializedName("lyrics") val lyrics: String
) {
    fun getLyricsMap(): Map<Int, String> {
        val map = mutableMapOf<Int, String>()
        lyrics.split("\n").forEach {
            it.split("]").also { itt ->
                map[getTimeStringToInt(itt[0].drop(1))] = itt[1]
            }
        }
        return map
    }

    fun getLyricsList(): List<Pair<Int, String>> {
        return lyrics.split("\n").map { it.split("]").let { itt -> getTimeStringToInt2(itt[0].drop(1)) to itt[1] } }
    }

    private fun getTimeStringToInt(time: String): Int {
        var str = ""
        time.filter { it != ':' }.forEach {
            if(str.isEmpty()) {
                if(it != '0') str += it
            } else {
                str += it
            }
        }
        Timber.d("getTimeStringToInt : $str")
        return str.toInt()
    }

    private fun getTimeStringToInt2(time: String): Int {
        var str = ""
        var minute = 0
        var sec = 0
        var milli = ""
        time.split(":").also {
            minute = it[0].toInt()
            sec = it[1].toInt()
            milli = it[2]
        }
        sec += minute * 60
        str = "$sec$milli"
        Timber.d("getTimeStringToInt : $str")
        return str.toInt()
    }
}
