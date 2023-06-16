package com.bangkit.sapigo.data.api

import com.bangkit.sapigo.data.Login
import com.bangkit.sapigo.data.Register
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiServiceAuth {

    @POST("api/register")
    @FormUrlEncoded
    fun doRegister(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<Register>

    @POST("api/login")
    @FormUrlEncoded
    fun doLogin(
        @Field("email") email:String,
        @Field("password") password: String
    ): Call<Login>

    @PUT("/users:id")
    @FormUrlEncoded
    fun doUpdate(
        @Field ("username") username: String,
        @Field ("email") email: String,
        @Field ("password") password: String
    )
}