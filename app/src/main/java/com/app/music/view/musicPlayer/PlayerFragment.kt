package com.app.music.view.musicPlayer

import android.content.*
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.app.music.R
import com.app.music.databinding.MainScreenLayoutBinding
import com.app.music.domain.Track
import com.app.music.domain.User
import com.app.music.service.androidservices.InternetCallback
import com.app.music.service.androidservices.InternetStatus
import com.app.music.service.androidservices.InternetStatusReceiver
import com.app.music.service.androidservices.MusicService
import com.app.music.service.handlers.PhoneCallReceiverHandler
import com.app.music.service.restservices.SearchManager
import com.app.music.view.login.LOG_TAG
import com.app.music.view.login.LoginFragment
import com.app.music.view.login.Mode
import com.app.music.view.login.RegisterFragment
import com.app.music.viewModel.login.LoginViewModel
import com.app.music.viewModel.musicPlayer.PlayerViewModel
import java.lang.Exception
import java.util.*


private const val SEARCH_BACKSTACK_NAME = "search_backstack"
private const val MAX_PROGRESS_BAR = 30
private const val TRACK_IMAGE_SIZE = "300x300"
class PlayerFragment: Fragment() {
    private lateinit var viewModel: PlayerViewModel
    private lateinit var viewModelLogin: LoginViewModel
    private lateinit var binding: MainScreenLayoutBinding
    private lateinit var user: User

    private var playing = false
    private var interactedWithPhonecall = false
    private var isCalling = false
    private lateinit var musicService: MusicService
    private var mBound: Boolean = false
    private var internetStatusReceiver: InternetStatusReceiver = InternetStatusReceiver.getInstance()
    private var internetStatus = InternetStatus.OFFLINE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.main_screen_layout, container, false)
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            user = requireArguments().getSerializable(LoginFragment.USER_EXTRA) as User
            Log.d(LOG_TAG, "Logged in user: $user")
        }catch (ex: Exception){
            Log.d(LOG_TAG,ex.message.toString())
        }

        initialize()
    }

    private fun initialize(){
        val activity = requireActivity()
        viewModel = ViewModelProvider(activity).get(PlayerViewModel::class.java)
        viewModelLogin = ViewModelProvider(activity).get(LoginViewModel::class.java)
        binding.imageSearch.setOnClickListener {
            onBtnSearch()
        }

        binding.imagePlayPause.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                onButtonPlay()
            }
        }
        binding.profileMenu.setOnClickListener{
            onBtnAccount()
        }

        initObservers()
    }

    /** Defines callbacks for service binding, passed to bindService()  */
    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to MusicService, cast the IBinder and get MusicService instance
            val binder = service as MusicService.MusicBinder
            musicService = binder.getService()
            mBound = true
            Log.d(LOG_TAG,"on service connected")

        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onButtonPlay() {
        if(playing){
            setPlayIcon()
            pauseAudio()
        }else {
            if(internetStatus == InternetStatus.OFFLINE){
                Toast.makeText(requireContext(), "No internet connection!", Toast.LENGTH_SHORT).show()
                return
            }
            viewModel.liveTrack.value?.let { it1 -> playAudio(it1.previewURL) }
            setPauseIcon()
            Log.d(LOG_TAG, "play main")
        }
        playing=!playing
    }

    private fun setPauseIcon(){
        binding.imagePlayPause.setImageResource(R.drawable.ic_pause)
    }
    private fun setPlayIcon(){
        binding.imagePlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24)
    }

    private fun pauseAudio() {
        musicService.pause()
        Log.d(LOG_TAG, "pause main")
    }

    private fun initObservers() {
        viewModel.liveTrack.observe(viewLifecycleOwner,{
            setTrack(it)
        })

        PhoneCallReceiverHandler.isCalling.observeForever{
            Log.d(LOG_TAG, "in phonecall receiver isCalling: $it | playing: $playing")
            if(it!=isCalling) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (it && playing) {
                        interactedWithPhonecall = true
                        onButtonPlay()
                    } else {
                        if (interactedWithPhonecall && !playing)
                            onButtonPlay()
                        interactedWithPhonecall = false
                    }
                }
                isCalling = it
            }

        }

        registerInternetStatusReceiver()
    }

    private fun setTrack(track: Track) {
        binding.progressBar.progress = 0
        binding.progressBar.max = MAX_PROGRESS_BAR
        playing = false
        setPlayIcon()
        binding.textTitle.text = track.title
        binding.textArtist.text = track.artistName
        Glide.with(this).load(SearchManager.getUrlWithHeaders(track.albumId, TRACK_IMAGE_SIZE))
            .into(binding.trackImage)
    }

    private fun onBtnSearch() {
        Log.d(LOG_TAG, "onClick: search pressed")

        startSearchFragment()
    }
    private fun onBtnAccount(){
        Log.d(LOG_TAG, "onClick: profileMenu pressed")

        startAccountFragment()
    }

    private fun startSearchFragment(){
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack(SEARCH_BACKSTACK_NAME)
            .replace(R.id.fragment_container_view_player, SearchFragment())
            .commit()
    }
    private fun startAccountFragment(){
        viewModelLogin.sendMode(Mode.READ_ONLY)
        viewModelLogin.sendUser(user)
        requireActivity().supportFragmentManager.beginTransaction()
            .addToBackStack(RegisterFragment::class.java.name)
            .replace(R.id.fragment_container_view_player,RegisterFragment())
            .commit()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playAudio(audioURL: String) {
        musicService.play(audioURL)
        Toast.makeText(requireContext(), "Audio started playing..", Toast.LENGTH_SHORT).show()
    }

    private fun registerInternetStatusReceiver() {
        internetStatusReceiver.setOnCallbackReceivedListener(object : InternetCallback {
            override fun onStatusChanged(status: InternetStatus) {
                internetStatus = status
            }
        })
        requireActivity().registerReceiver(
            internetStatusReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    /**
     * Manage app behaviour when a phone call is handled by the system
     */
    override fun onStart() {
        super.onStart()
        PhoneCallReceiverHandler.registerPhonecallReceiver(requireContext(), requireActivity())
        Intent(requireContext(), MusicService::class.java).also { intent ->
            requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
            if(mBound) {
                musicService.isPlaying.observe(this@PlayerFragment.viewLifecycleOwner, {
                    Log.d(LOG_TAG, "music is playing: $it")
                    if (!it) {
                        this@PlayerFragment.setPlayIcon()
                        this@PlayerFragment.playing = it
                        binding.progressBar.progress = MAX_PROGRESS_BAR
                    }
                })
                musicService.progress.observe(this@PlayerFragment.viewLifecycleOwner, {
                    Log.d(LOG_TAG, "progress update: $it")
                    binding.progressBar.progress = it
                })
            }
        }

    }

    override fun onDestroy() {
        PhoneCallReceiverHandler.unregisterPhoneCallReceiver(requireActivity())
        requireActivity().unbindService(connection)
        mBound = false
        super.onDestroy()
    }

}