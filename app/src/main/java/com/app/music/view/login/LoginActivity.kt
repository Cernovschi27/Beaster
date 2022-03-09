package com.app.music.view.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.app.music.R
import com.app.music.databinding.ActivityLoginBinding

const val LOG_TAG = "Gabi"
class LoginActivity : AppCompatActivity() {

    companion object {
        lateinit var instance: LoginActivity
            private set
    }

    private lateinit var binding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        instance=this
        startLoginFragment()

    }

    private fun startLoginFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_login, LoginFragment())
            .commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}