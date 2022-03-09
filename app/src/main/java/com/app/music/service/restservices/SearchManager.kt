package com.app.music.service.restservices

import android.content.ContentValues
import android.util.Log
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.app.music.domain.SearchResponse
import retrofit2.Response

private const val apikey = " NGNjYzM5NjktNDliNy00MWM5LWI1NjktNDNiMGVlY2E3NzNh"
class SearchManager {

    private var searchService: ISearchService = RetrofitService.searchService

    suspend fun searchTracks(trackName: String): Response<SearchResponse> {
        val response: Response<SearchResponse> = searchService.searchTracks(
            trackName = trackName
        )

        Log.d(ContentValues.TAG, " response $response")
        return response
    }

    companion object{
        /**
         * @param albumId - the id of the album
         * @return glideUrl - a glide URL for requesting the given album cover image
         */
        fun getUrlWithHeaders(albumId: String, size: String = "70x70"): GlideUrl {
            val url = "https://api.napster.com/imageserver/v2/albums/$albumId/images/$size.jpg"
            return GlideUrl(
                url, LazyHeaders.Builder()
                    .addHeader("apikey", apikey)
                    .build()
            )
        }
    }

}