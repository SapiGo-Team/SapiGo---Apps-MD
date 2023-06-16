package com.bangkit.sapigo.ui.scan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.bangkit.sapigo.BaseActivity
import com.bangkit.sapigo.MainActivity
import com.bangkit.sapigo.data.database.MyDatabaseHelper
import com.bangkit.sapigo.databinding.ActivityResultBinding

class ResultActivity : BaseActivity() {

    private lateinit var binding: ActivityResultBinding
    private var isActivityFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setProgressBar(binding.progressBar)
        setTextLoading(binding.tvLoading)

        handleIntentData(intent)

        showTextLoading("Please kindly wait for your result")

        binding.btnHome.isEnabled = false

        binding.btnHome.setOnClickListener {
            if (isActivityFinished) {
                val intent = Intent(this@ResultActivity, MainActivity::class.java)
                intent.putExtra("navHomeFragment", true)
                startActivity(intent)
            } else {
                Toast.makeText(this@ResultActivity, "Please wait for the data to load first", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnSave.setOnClickListener {
            val responses = arrayListOf(
                binding.tvGumResponse.text.toString(),
                binding.tvFeetResponse.text.toString(),
                binding.tvSalivaResponse.text.toString(),
                binding.tvTongueResponse.text.toString()
            )

            val compiledImageUri = intent.getParcelableExtra<Uri>("COMPILED_IMAGE_URI")
            compiledImageUri?.let { uri ->
                try {
                    storeData(responses, uri)
                } catch (e : Exception){
                    Log.e("error","Error")
                }

            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        showProgressBar()

        Handler(Looper.getMainLooper()).postDelayed({
            handleIntentData(intent)
            showTextLoading("Here is your Result")
            hideProgressBar()
            binding.btnHome.isEnabled = true
            isActivityFinished = true
        }, 2000) // 2 seconds delay before handling the intent data
    }

    private fun handleIntentData(intent: Intent) {
        val compiledImageUri = intent.getParcelableExtra<Uri>("COMPILED_IMAGE_URI")
        compiledImageUri?.let { uri ->
            binding.imgResult.setImageURI(uri)
        }

        val responses = intent.getStringArrayListExtra("RESPONSES")

        // Check if the responses list is not null and its size matches the number of TextViews
        if (responses != null && responses.size == 4) {
            binding.tvGumResponse.text = responses[0].replace("\"", "")
            binding.tvFeetResponse.text = responses[1].replace("\"", "")
            binding.tvSalivaResponse.text = responses[2].replace("\"", "")
            binding.tvTongueResponse.text = responses[3].replace("\"", "")
        }
    }

    private fun storeData(responses: ArrayList<String>, compiledImageUri: Uri) {
        val databaseHelper = MyDatabaseHelper(this)

        // Generate a unique identifier for the image
        val imageId = System.currentTimeMillis().toString()

        // Append the imageId to the image file name in the URI
        val imageUriWithId = Uri.parse("$compiledImageUri?id=$imageId")

        databaseHelper.insertData(imageUriWithId, responses)
        databaseHelper.close()

        // Show a toast message indicating successful data save
        Toast.makeText(this, "Data saved successfully", Toast.LENGTH_SHORT).show()
    }

}
