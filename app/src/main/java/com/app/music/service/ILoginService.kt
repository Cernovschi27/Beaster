package com.app.music.service

import com.app.music.domain.User

interface ILoginService {
    /**
     * Attempts to find the user with the given credentials in the persistent storage
     * @param email the user's email
     * @param password the user's password
     *
     * @return The user with the given credentials if it exists, null otherwise
     */
    suspend fun logIn(email: String, password: String): User?

    /**
     * saves the given user into the persistent storage
     * @param user the User to be saved
     */
    suspend fun register(user: User)

    /**
     * Attempts to find a user in SharedPreferences
     * @return the user if it exists, null otherwise
     */
    fun getUserFromSharedPreferences(): User?

    /**
     * updates the user from sharedPreferences
     * @param user the User with the new data which will be updated
     */
    fun updateSharedPrefUser(user: User)
}