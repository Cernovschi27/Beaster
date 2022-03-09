package com.app.music.view.musicPlayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.music.R
import com.app.music.view.login.LoginFragment
import com.app.music.databinding.ActivityMusicPlayerBinding

class PlayerActivity:AppCompatActivity() {

    private lateinit var binding: ActivityMusicPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_player)
        startMusicPlayerFragment()
    }

    private fun startMusicPlayerFragment() {
        val fragment = PlayerFragment()
        val bundle = Bundle()
        bundle.putSerializable(LoginFragment.USER_EXTRA, intent.getSerializableExtra(LoginFragment.USER_EXTRA))
        fragment.arguments= bundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_player, fragment)
            .commit()
    }
}