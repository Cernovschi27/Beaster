package com.app.music.viewModel.musicPlayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.music.domain.Track
import com.app.music.service.IPlayerService
import com.app.music.service.PlayerService
import java.util.*

class PlayerViewModel: ViewModel() {
    private val playerService: IPlayerService = PlayerService.getInstance()

    private val _mutableTrack = MutableLiveData<Track>()
    val liveTrack : LiveData<Track> get() = _mutableTrack

    private val searchObservable = object : Observable(){
        fun notify(tracks: List<Track>){
            setChanged()
            notifyObservers(tracks)
        }
    }
    fun onSearch(name: String) {
        playerService.searchTrack(name).addObserver { _, arg ->
            if(arg is ArrayList<*>) {
                val tracks = arg.filterIsInstance<Track>()
                searchObservable.notify(tracks)
                _mutableTrack.value = tracks.first()
            }

        }
    }

    fun observeSearch(observer: Observer) {
        searchObservable.addObserver(observer)
    }

    fun sendTrack(item: Track) {
        _mutableTrack.value=item
    }

}