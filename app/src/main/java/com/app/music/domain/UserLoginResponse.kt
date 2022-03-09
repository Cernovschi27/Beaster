package com.app.music.domain

sealed class UserLoginResponse  {
    class Success(val user:User) :UserLoginResponse()
    class Failed (val message:String):UserLoginResponse()
}