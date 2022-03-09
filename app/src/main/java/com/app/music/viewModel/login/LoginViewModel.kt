package com.app.music.viewModel.login

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.textfield.TextInputLayout
import com.app.music.R
import androidx.lifecycle.*
import com.app.music.domain.User
import com.app.music.domain.UserLoginResponse
import com.app.music.exceptions.LoginException
import com.app.music.persistence.LoginRepository
import com.app.music.service.ILoginService
import com.app.music.service.LoginService

import com.app.music.view.login.Mode
import kotlinx.coroutines.*


class LoginViewModel : ViewModel() {

    private val _mutableUser = MutableLiveData<User>()
    var user: LiveData<User> = _mutableUser
    private val _mutableMode = MutableLiveData(Mode.DEFAULT)
    var mode: LiveData<Mode> = _mutableMode

    private lateinit var repository: LoginRepository
    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        country: String,
        phoneNumber: String,
        profilePhoto: Uri
    ) {
        val user = User(
            firstName,
            lastName,
            email,
            password,
            country,
            phoneNumber,
            profilePhoto.toString()
        )
        if (mode.value == Mode.EDITABLE)
            loginService.updateSharedPrefUser(user)
        else {
            try {
                viewModelScope.launch(Dispatchers.IO) {
                    loginService.register(user)
                }
            } catch (ex: LoginException) {
                throw ex
            }
        }
    }

    fun logIn(email: String, password: String): LiveData<UserLoginResponse> {
        val userLoginResponseLiveData = MutableLiveData<UserLoginResponse>()

        viewModelScope.launch(Dispatchers.IO) {
            if (email.isBlank() || password.isBlank()) {
                userLoginResponseLiveData.postValue(UserLoginResponse.Failed("Empty Field"))
            } else {

                val response = loginService.logIn(email, password)
                if (response == null) {
                    userLoginResponseLiveData.postValue(UserLoginResponse.Failed("Failed"))
                } else {
                    userLoginResponseLiveData.postValue(UserLoginResponse.Success(response))
                }
            }
        }
        return userLoginResponseLiveData


    }

    fun getSharedPreferencesUser(): User? {
        return loginService.getUserFromSharedPreferences()
    }

    private val loginService: ILoginService = LoginService.getInstance()
    //aaa@a.com     Abcd1!


    fun sendUser(user: User) {
        _mutableUser.value = user
    }

    fun sendMode(mode: Mode) {
        _mutableMode.value = mode
    }

    /**
     * sets text input layout stroke color
     * depending if the given text matches the given check function or not
     */
    fun updateStroke(text: CharSequence?, layout: TextInputLayout, check: (t: CharSequence)->Boolean){
        if(check(text!!)) {//set stroke color to green if valid
            layout.boxStrokeColor = Color.GREEN
            layout.endIconDrawable = null
        }
        else//set stroke color to red
            layout.boxStrokeColor = Color.RED
    }

    /**
     * sets an error icon as the endIcon on the given layout for the invalid input
     */
    fun highlightWrong(layout: TextInputLayout){
        layout.boxStrokeColor = Color.RED
        layout.setEndIconDrawable(R.drawable.ic_invalid)
        layout.setEndIconTintList(ColorStateList.valueOf(Color.RED))
        layout.setEndIconActivated(true)
        layout.endIconMode=TextInputLayout.END_ICON_CUSTOM
    }

}