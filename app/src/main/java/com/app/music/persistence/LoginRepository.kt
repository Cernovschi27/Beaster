package com.app.music.persistence

import androidx.lifecycle.LiveData
import com.app.music.domain.User

class LoginRepository(private val userDao:UserDao): ILoginRepository {
    companion object{
        fun getInstance(userDao: UserDao) = LoginRepository(userDao)
    }
    private val readAllUsers:LiveData<List<User>> =userDao.readAllUsers()
    override suspend fun find(user: User): User? {
        return userDao.getUser(user.email)
    }
    override suspend fun save (user: User){
        userDao.register(user)
    }
}