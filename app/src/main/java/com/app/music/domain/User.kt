package com.app.music.domain

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import java.io.Serializable
@Entity(tableName = "user_table")
data class User(
    @ColumnInfo
    var firstName:String,
    @ColumnInfo
    var lastName:String,
    @ColumnInfo
    var email: String,
    @ColumnInfo
    var password: String,
    @ColumnInfo
    var country: String = "",
    @ColumnInfo
    var phoneNumber: String = "",
    @ColumnInfo
    var profilePhoto: String = "",
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
): Serializable{
    constructor(email: String, password: String) : this("","",email, password)

    /**
     * @return the Uri of the user's profile image
     */
    fun getImageURI(): Uri{
        return Uri.parse(profilePhoto)
    }
}
