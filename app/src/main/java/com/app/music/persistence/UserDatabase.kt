package com.app.music.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.music.domain.User

@Database(entities = [User::class],version = 1,exportSchema = false)
abstract class UserDatabase:RoomDatabase() {
    abstract fun userDao():UserDao
    companion object{
        @Volatile
        private var INSTANCE:UserDatabase?=null
        fun getDatabase(context: Context):UserDatabase{
            val tempInstante= INSTANCE
            //check if our instance already exist
            if(tempInstante!=null) {
                return tempInstante
            }
            //protect from concurent execution by multiple threads
            synchronized(this){
                val instance= Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE=instance
                return instance
            }
        }
    }

}