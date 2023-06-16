package com.bangkit.sapigo.data.api

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService1 {

    @Multipart
    @POST("/predict/gum")
    fun getPredictGum(
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>

    @Multipart
    @POST("/predict/tongue")
    fun getPredictTongue(
        @Part file: MultipartBody.Part,
    ): Call<ResponseBody>

    @Multipart
    @POST("/predict/feet")
    fun getPredictFeet(
        @Part file: MultipartBody.Part,
    ): Call<ResponseBody>

    @Multipart
    @POST("/predict/saliva")
    fun getPredictSaliva(
        @Part file: MultipartBody.Part,
    ): Call<ResponseBody>


}