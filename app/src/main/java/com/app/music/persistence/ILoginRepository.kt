package com.app.music.persistence

import com.app.music.domain.User

interface ILoginRepository {
    /**
     * @param user the user to be found
     * @return the User if it was found, or null if it doesn't exist
     */
    suspend fun find(user: User): User?

    /**
     * saves the given user to the persistent storage
     * @param user the user to be stored
     */
    suspend fun save(user: User)
}