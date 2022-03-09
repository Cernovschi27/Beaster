package com.app.music.service.restservices

import com.app.music.domain.SearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ISearchService {
    /**
     *  set request header of type json and provide apikey
     */
    @Headers(
        "contentType: application/json",
        "apikey: NGNjYzM5NjktNDliNy00MWM5LWI1NjktNDNiMGVlY2E3NzNh"
    )
    @GET("v2.2/search")
    suspend fun searchTracks(@Query("query") trackName: String, @Query("type") type: String = "track"): Response<SearchResponse>

}