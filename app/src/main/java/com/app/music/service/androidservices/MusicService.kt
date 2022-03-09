package com.app.music.service.androidservices

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Icon
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.music.R
import com.app.music.view.login.LOG_TAG
import java.io.IOException


class MusicService : Service() {

    companion object {
        const val CHANNEL_ID = "CHANNEL_ID"
        const val NOTIFICATION_ID = 5115
    }

    private val _mutableIsPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _mutableIsPlaying

    private val _mutableProgress = MutableLiveData<Int>()
    val progress: LiveData<Int> get() = _mutableProgress

    private var currentTrack = ""

    private val seekHandler = Handler()

    private val updateSeekBar: Runnable = object : Runnable {
        override fun run() {
            //val totalDuration = mediaPlayer.duration.toLong()
            val currentDuration = mediaPlayer.currentPosition.toLong()

            // Updating progress bar
            _mutableProgress.value = milliSecondsToTimer(currentDuration)

            // Call this thread again after 15 milliseconds => ~ 1000/60fps
            seekHandler.postDelayed(this, 15)
        }
    }

    private val mediaPlayer = MediaPlayer()
    //apparently there is no API to check if the mediaPlayer is paused?!
    private var isPaused = false
    private val binder = MusicBinder()
    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    fun milliSecondsToTimer(milliseconds: Long): Int {
        return (milliseconds/1000).toInt()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }
    private fun playAudio(audioURL: String) {
            Log.d(LOG_TAG, "trying to start player")

            try {
                mediaPlayer.setDataSource(audioURL)
                mediaPlayer.prepareAsync()
                currentTrack = audioURL
            } catch (e: IOException) {
                e.printStackTrace()
            }

    }

    private fun resetPlayer(){
        mediaPlayer.stop()
        mediaPlayer.reset()
    }

    fun pause(){
        isPaused = if(mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            true
        }else{
            false
        }
    }

    private fun resume(){
        //mediaPlayer.seekTo(mediaPlayer.currentPosition)
        mediaPlayer.start()
        isPaused = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun play(url: String){
        Log.d(LOG_TAG, "play")
        Log.d(LOG_TAG, "trackURL: $url; $isPaused")
        //if the song was paused
        if(isPaused && isPlaying.value == true && url==currentTrack){
            resume()
        }else {
            if (mediaPlayer.isPlaying || url!=currentTrack)
                resetPlayer()

            startForeground(NOTIFICATION_ID, createNotification())
            playAudio(url)

        }
        isPaused = false
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        Log.d(LOG_TAG, "onCreate")

        //set media player attributes
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        //wake lock to keep resources while running in the background
        //mediaPlayer.setWakeMode(applicationContext, PowerManager.PARTIAL_WAKE_LOCK)

        mediaPlayer.setOnCompletionListener {
            //stop the notification and foreground service when the music stops playing
            _mutableIsPlaying.value = false
            resetPlayer()
            Log.d(LOG_TAG, "song done")
            seekHandler.removeCallbacksAndMessages(null)
            this.stopForeground(true)
        }
        //when the player is done preparing async, the track can be played
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.seekTo(0)
            mediaPlayer.start()
            _mutableIsPlaying.value = true
            seekHandler.postDelayed(updateSeekBar, 15)
        }

        createNotificationChannel()

    }


    override fun onDestroy() {
        Log.d(LOG_TAG, "onDestroy")
        seekHandler.removeCallbacks(updateSeekBar)
        mediaPlayer.release()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(): Notification {

        return Notification.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_foreground_music_service)
            .setLargeIcon(Icon.createWithResource(applicationContext,R.drawable.player_notification_icon))
            .setContentTitle("TANANANA")
            .setContentText("Yaay, your song is playing")
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        serviceChannel.enableLights(true)
        serviceChannel.lightColor= Color.BLUE
        serviceChannel.setSound(null, null)
        manager.createNotificationChannel(serviceChannel)
    }
}