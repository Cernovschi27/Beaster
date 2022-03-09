package com.app.music.domain

import com.google.gson.annotations.SerializedName


data class TrackSearchResult(
    @SerializedName("data")
    var data: TrackList
)