package com.bangkit.sapigo.data.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.bangkit.sapigo.data.api.ApiConfig1
import com.bangkit.sapigo.ui.scan.ResultActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class PreviewViewModel(private val application: Application): ViewModel() {

    val gumResponse: MutableLiveData<String> = MutableLiveData()
    val feetResponse: MutableLiveData<String> = MutableLiveData()
    val salivaResponse: MutableLiveData<String> = MutableLiveData()
    val tongueResponse: MutableLiveData<String> = MutableLiveData()

    val response:MutableLiveData<String> = MutableLiveData()


    constructor() : this(application = Application()) {
        // Empty constructor
    }

     private fun processImageWithGumPrediction(image: File) {
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            image.name,
            requestImageFile
        )
        val client = ApiConfig1.getInterfaceApi().getPredictGum(imageMultiPart)

        client.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    gumResponse.postValue(responseBody!!)

                    Log.d("Successful", responseBody.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", "onFailure Call: ${t.message}")
            }
        })
    }

     private fun processImageWithFeetPrediction(image: File) {
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            image.name,
            requestImageFile
        )
        val client = ApiConfig1.getInterfaceApi().getPredictFeet(imageMultiPart)

        client.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    feetResponse.postValue(responseBody!!)
                    Log.d("Successful", responseBody.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", "onFailure Call: ${t.message}")
            }
        })
    }

     private fun processImageWithSalivaPrediction(image: File) {
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            image.name,
            requestImageFile
        )
        val client = ApiConfig1.getInterfaceApi().getPredictSaliva(imageMultiPart)

        client.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    salivaResponse.postValue(responseBody!!)
                    Log.d("Successful", responseBody.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", "onFailure Call: ${t.message}")
            }
        })
    }
     private fun processImageWithTonguePrediction(image: File) {
        val requestImageFile = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            image.name,
            requestImageFile
        )
        val client = ApiConfig1.getInterfaceApi().getPredictTongue(imageMultiPart)

        client.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()?.string()
                    tongueResponse.postValue(responseBody!!)
                    Log.d("Successful", responseBody.toString())
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Error", "onFailure Call: ${t.message}")
            }
        })
    }

    fun uploadImages(images: List<File>, context: Context) {
        val responses = mutableListOf<String?>()
        val totalImages = images.size
        var completedImages = 0

        for (image in images) {
            when (images.indexOf(image)) {
                0 -> processImageWithGumPrediction(image)
                1 -> processImageWithFeetPrediction(image)
                2 -> processImageWithSalivaPrediction(image)
                3 -> processImageWithTonguePrediction(image)
                else -> throw IllegalArgumentException("Invalid image index: ${images.indexOf(image)}")
            }
        }

        // Observe the responses and check if all images are processed
        val observer = Observer<String?> { response ->
            responses.add(response)
            completedImages++
            checkCompletion(totalImages, completedImages, responses, context)
        }

        gumResponse.observeForever(observer)
        feetResponse.observeForever(observer)
        salivaResponse.observeForever(observer)
        tongueResponse.observeForever(observer)
    }

    private fun checkCompletion(
        totalImages: Int,
        completedImages: Int,
        responses: List<String?>,
        context: Context
    ) {
        // Check if all images are processed
        if (completedImages == totalImages) {
            // Start the ResultActivity and pass the responses via intent
            val intent = Intent(context, ResultActivity::class.java)
            intent.putStringArrayListExtra("RESPONSES", ArrayList(responses))
            context.startActivity(intent)
        }
    }
    fun resetData() {
        gumResponse.value = null
        feetResponse.value = null
        salivaResponse.value = null
        tongueResponse.value = null
    }

}