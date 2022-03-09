package com.app.music.view.login

import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.app.music.domain.User
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.app.music.databinding.LoginFragmentBinding
import com.app.music.exceptions.LoginException
import com.app.music.R
import com.app.music.domain.UserLoginResponse
import com.app.music.service.androidservices.InternetCallback
import com.app.music.service.androidservices.InternetStatus
import com.app.music.service.androidservices.InternetStatusReceiver
import com.app.music.view.musicPlayer.PlayerActivity
import com.app.music.viewModel.login.LoginViewModel

class LoginFragment : Fragment() {

    companion object {
        const val USER_EXTRA = "user_extra"
        val BACKSTACK_TAG: String = RegisterFragment::class.java.name
    }

    private var internetStatusReceiver: InternetStatusReceiver = InternetStatusReceiver.getInstance()

    private lateinit var binding: LoginFragmentBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.login_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initialize()

    }

    private fun autocompleteCredentials() {
        val user = viewModel.getSharedPreferencesUser()
        if (user != null) {
            binding.editTextEmail.setText(user.email)
            binding.editTextPassword.setText(user.password)

//            if (user.getImageURI() != Uri.EMPTY)
//                binding.profileImage.setImageURI(user.getImageURI())
        }
    }


    private fun initialize() {
        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)

        binding.buttonLogin.setOnClickListener {
            onLogin()
        }

        binding.buttonRegister.setOnClickListener {
            onRegister()
        }
        binding.buttonEditInfo.setOnClickListener {
            onEditMyInfo()
        }


    }

    override fun onStart() {
        super.onStart()
        autocompleteCredentials()
        registerInternetStatusReceiver()
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(internetStatusReceiver)
    }

    private fun registerInternetStatusReceiver() {
        internetStatusReceiver.setOnCallbackReceivedListener(object : InternetCallback {
            override fun onStatusChanged(status: InternetStatus) {
                when(status){
                    InternetStatus.ONLINE-> binding.onlineInformator.setBackgroundColor(Color.GREEN)
                    InternetStatus.OFFLINE-> binding.onlineInformator.setBackgroundColor(Color.RED)
                }
            }
        })
        requireActivity().registerReceiver(
            internetStatusReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    private fun onRegister() {
        viewModel.sendMode(Mode.DEFAULT)
        viewModel.sendUser(User(getString(R.string.empty), getString(R.string.empty)))
        startRegisterFragment()
    }

    private fun onLogin() {
        viewModel.logIn(
            binding.editTextEmail.text.toString(),
            binding.editTextPassword.text.toString()
        ).observe(viewLifecycleOwner) {
            when (it) {
                is UserLoginResponse.Success -> {
                    startMusicPlayerActivity(it.user)
                }
                is UserLoginResponse.Failed -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    clearFields()
                }
            }

        }


    }

    private fun onEditMyInfo() {
        try{
            val user = viewModel.getSharedPreferencesUser()
            Log.d(LOG_TAG, "on editInfo : $user")
            //if there is a user in the shared preferences, open the edit screen
            //otherwise redirect to register
            if (user != null)
                startEditInfoActivity(user)
            else
                startRegisterFragment()
        } catch (exception: LoginException) {
            Toast.makeText(requireContext(), exception.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startEditInfoActivity(user: User) {
        //set user and editable mode for edit info fragment
        //--- it uses the register fragment
        viewModel.sendMode(Mode.EDITABLE)
        viewModel.sendUser(user)
        startRegisterFragment()
    }

    private fun clearFields() {
        binding.editTextEmail.setText(getString(R.string.empty))
        binding.editTextPassword.setText(getString(R.string.empty))
    }

    private fun startRegisterFragment() {
        Log.d(LOG_TAG, "starting register fragment")
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view_login, RegisterFragment())
                //add login fragment to backstack
            .addToBackStack(BACKSTACK_TAG)
            .commit()
    }

    private fun startMusicPlayerActivity(user: User) {
        val intent = Intent(requireActivity(), PlayerActivity::class.java)
        intent.putExtra(USER_EXTRA, user)
        startActivity(intent)
    }

}