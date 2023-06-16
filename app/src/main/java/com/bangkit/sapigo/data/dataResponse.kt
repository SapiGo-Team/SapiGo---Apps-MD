package com.bangkit.sapigo.data

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class dataResponse(val message: String)

data class Register(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("error")
    val error: String,

    @field:SerializedName("uid")
    val uid: String
)

data class Login(

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("error")
    val error: String,

)
