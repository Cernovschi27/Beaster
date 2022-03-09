package com.app.music.domain

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("artistId")
    var artistId: String,
    @SerializedName("artistName")
    var artistName: String,
    @SerializedName("name")
    var title: String,
    @SerializedName("albumName")
    var albumName: String,
    @SerializedName("albumId")
    var albumId: String,
    @SerializedName("previewURL")
    var previewURL: String
    )