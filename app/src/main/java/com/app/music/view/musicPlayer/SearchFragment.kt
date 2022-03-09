package com.app.music.view.musicPlayer

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.app.music.R
import com.app.music.databinding.SearchFragmentBinding
import com.app.music.domain.Track
import com.app.music.service.androidservices.InternetCallback
import com.app.music.service.androidservices.InternetStatus
import com.app.music.service.androidservices.InternetStatusReceiver
import com.app.music.view.adapter.OnTrackClickInterface
import com.app.music.view.adapter.TrackAdapter
import com.app.music.viewModel.musicPlayer.PlayerViewModel
import java.util.*

class SearchFragment: Fragment() {

    private lateinit var viewModel: PlayerViewModel
    private lateinit var adapter : TrackAdapter
    private var internetStatusReceiver: InternetStatusReceiver = InternetStatusReceiver.getInstance()
    private lateinit var binding: SearchFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.search_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()

    }

    private fun initialize() {
        viewModel = ViewModelProvider(requireActivity()).get(PlayerViewModel::class.java)

        binding.searchButton.setOnClickListener{
            if(isEmpty(binding.textInputSearch))
                Toast.makeText(requireContext(), "Are you searching for nothing ?", Toast.LENGTH_SHORT).show()
            else{
                val searchKey=binding.textInputSearch.text.toString()
                viewModel.onSearch(searchKey)
                viewModel.observeSearch { _, arg ->
                    if (arg is ArrayList<*>) {
                        val track = arg.filterIsInstance<Track>()
                        setRecyclerView(track)
                    }
                }
            }
        }

        registerInternetStatusReceiver()
    }

    private fun registerInternetStatusReceiver() {
        internetStatusReceiver.setOnCallbackReceivedListener(object : InternetCallback {
            override fun onStatusChanged(status: InternetStatus) {
                when(status){
                    InternetStatus.ONLINE-> {
                        binding.noInternetBanner.visibility = View.INVISIBLE
                        binding.searchButton.isClickable = true
                    }
                    InternetStatus.OFFLINE-> {
                        binding.noInternetBanner.visibility = View.VISIBLE
                        binding.searchButton.isClickable = false
                    }
                }
            }
        })
        requireActivity().registerReceiver(
            internetStatusReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    private fun setRecyclerView(tracks: List<Track>){
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        val clickListener: OnTrackClickInterface = object: OnTrackClickInterface{
            override fun onClick(item: Track) {
                viewModel.sendTrack(item)
                parentFragmentManager.popBackStack()
            }

        }
        adapter= TrackAdapter(tracks, clickListener)
        binding.recyclerviewTracks.adapter=adapter
        binding.recyclerviewTracks.layoutManager=layoutManager
    }

    private fun isEmpty(etText: TextInputEditText):Boolean{
        return etText.text.toString().trim().isEmpty()
    }
}