package com.app.music.service

import com.app.music.domain.SearchResponse
import com.app.music.domain.Track
import com.app.music.service.restservices.SearchManager
import kotlinx.coroutines.*
import retrofit2.Response
import java.util.*

class PlayerService : IPlayerService {
    companion object{
        private val playerService = PlayerService()
        fun getInstance() = playerService
    }

    private var service: SearchManager = SearchManager()

    private fun getTrackFromResponse(response: Response<SearchResponse>): List<Track> {
        return (response.body() as SearchResponse).searchResult.data.tracks
    }

    override fun searchTrack(query: String): Observable {

        val observableResponse = object : Observable() {
            fun notify(response: Response<SearchResponse>) {
                setChanged()
                notifyObservers(getTrackFromResponse(response))
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.searchTracks(query)
            withContext(Dispatchers.Main) {
                observableResponse.notify(response)
            }
        }
        return observableResponse

    }



}