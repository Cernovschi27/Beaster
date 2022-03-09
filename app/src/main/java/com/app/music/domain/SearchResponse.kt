package com.app.music.domain

import com.google.gson.annotations.SerializedName


data class SearchResponse(
    @SerializedName("meta")
    var meta: Meta,

    @SerializedName("search")
    var searchResult: TrackSearchResult

)
