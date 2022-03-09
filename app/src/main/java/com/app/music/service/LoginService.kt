package com.app.music.service

import android.content.Context
import android.util.Log
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.gson.Gson
import com.app.music.R
import com.app.music.domain.User
import com.app.music.persistence.ILoginRepository
import com.app.music.persistence.LoginRepository
import com.app.music.persistence.UserDatabase
import com.app.music.view.login.LOG_TAG
import com.app.music.view.login.LoginActivity
import java.lang.Exception

private const val userPreferenceFileKey = "user_pref_fk"
private const val userKey = "user_key"
class LoginService : ILoginService {

    companion object{
        private val loginService = LoginService()
        fun getInstance() = loginService
    }

    private val repo: ILoginRepository = LoginRepository.getInstance(UserDatabase.getDatabase(LoginActivity.instance.applicationContext).userDao())

    override suspend fun logIn(email: String, password: String): User?{
        //search if the email exist in database
        val userFind= repo.find(User(email,password))
        if(userFind==null)
            return null
        //verify if input password match with the password from database(password that was hashed)
        val response=BCrypt.verifyer().verify(password.toCharArray(),userFind.password)
        if(response.verified==true)
            return userFind
        else
            return null
    }

    override suspend fun register(user: User) {
        updateSharedPreferences(user)
        val passHash= BCrypt.withDefaults().hashToString(12,user.password.toCharArray())
        user.password=passHash
        repo.save(user)
    }

    /**
     * saves/overrides the current saved user in SharedPreferences
     */
    private fun updateSharedPreferences(user: User) {
        val userJson = Gson().toJson(user)
        saveToSharedPreferences(userJson)
    }

    /**
     * saves the given user in Json format to shared preferences
     */
    private fun saveToSharedPreferences(userJson: String?) {
        val prefFile = LoginActivity.instance.getSharedPreferences(userPreferenceFileKey, Context.MODE_PRIVATE)
        prefFile.edit().putString(userKey, userJson).apply()
    }

    /**
     * returns the user saved in SharedPreferences
     *  or null if none is found
     */
    override fun getUserFromSharedPreferences(): User? {
        val prefFile =
            LoginActivity.instance.getSharedPreferences(userPreferenceFileKey, Context.MODE_PRIVATE)
        try {
            val user = Gson().fromJson(prefFile.getString(userKey,
                LoginActivity.instance.applicationContext.getString(R.string.empty)),
                User::class.java)
            Log.d(LOG_TAG, "shared pref user: $user")
            return user
        } catch (ex: Exception) {
            Log.d("LOG_TAG", ex.message.toString())
        }
        return null
    }

    override fun updateSharedPrefUser(user: User) {
        val current = getUserFromSharedPreferences()
        if(current!=null){
            if(user.password.isEmpty())
                user.password=current.password
            if(user.email.isEmpty())
                user.email=current.email
        }
        saveToSharedPreferences(Gson().toJson(user))
    }
}