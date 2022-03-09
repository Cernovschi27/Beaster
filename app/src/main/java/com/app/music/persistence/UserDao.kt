package com.app.music.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.music.domain.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun register(user: User)

    @Query("Select * from user_table order by id ASC")
    fun readAllUsers():LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE email =:userEmail")
    fun getUser(userEmail: String): User?
}