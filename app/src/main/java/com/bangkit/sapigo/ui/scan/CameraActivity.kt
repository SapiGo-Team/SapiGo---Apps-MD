package com.bangkit.sapigo.ui.scan

import androidx.activity.result.launch
import androidx.core.content.FileProvider
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bangkit.sapigo.databinding.ActivityCameraBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bangkit.sapigo.R
import com.bangkit.sapigo.utils.Helpers


class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageCapture: ImageCapture
    private lateinit var openGalleryLauncher: ActivityResultLauncher<Intent>
    private lateinit var takePhotoLauncher: ActivityResultLauncher<Uri>
    private lateinit var cameraOutputDirectory: File
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var sharedPreferences: SharedPreferences
    private var currentImageIndex = 0
    private val imageFiles = mutableListOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initGallery()
        cameraExecutor = Executors.newSingleThreadExecutor()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        binding.btnShutter.setOnClickListener {
            takePhoto()
        }
        binding.btnGallery.setOnClickListener {
            startGallery()
        }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnSwitch.setOnClickListener {
            cameraSelector =
                if(cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            startCamera()
        }
        sharedPreferences = getSharedPreferences("My_prefs", Context.MODE_PRIVATE)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
//            if (data?)
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, preview)
            } catch (e: Exception) {
                Log.e(TAG, "Error starting camera: ${e.message}", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val outputDirectory = getOutputDirectory()
        val photoFileName = "photo_${System.currentTimeMillis()}.jpg"
        val photoFile = File(outputDirectory, photoFileName)
        imageFiles.add(photoFile)

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputFileOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Image saved successfully
                    val savedUri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)

                    // Display Toast message on the main thread
                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, "Photo saved: $savedUri", Toast.LENGTH_SHORT).show()
                    }

                    if (imageFiles.size >= 4 && imageFiles.size % 4 == 0) {
                        val intent = Intent(this@CameraActivity, PreviewActivity::class.java)
                        val filePaths = imageFiles.map { it.absolutePath }
                        intent.putStringArrayListExtra(PreviewActivity.EXTRA_PHOTO_RESULT, ArrayList(filePaths))
                        intent.putExtra(PreviewActivity.EXTRA_CAMERA_MODE, cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
                        startActivity(intent)
                        imageFiles.clear()
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    // Error saving image
                    val errorMsg = "Error capturing image: ${exception.message}"

                    runOnUiThread {
                        Toast.makeText(this@CameraActivity, errorMsg, Toast.LENGTH_SHORT).show()
                    }

                    Log.e(TAG, errorMsg, exception)
                }
            }
        )
    }




    private fun initGallery() {
        openGalleryLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImages: List<Uri>? = result.data?.clipData?.let { clipData ->
                    (0 until clipData.itemCount).map { index ->
                        clipData.getItemAt(index).uri
                    }
                } ?: result.data?.data?.let { uri ->
                    listOf(uri)
                }

                if (!selectedImages.isNullOrEmpty()) {
                    val filePaths = selectedImages.mapNotNull { uri ->
                        Helpers.uriToFile(uri, this@CameraActivity)?.absolutePath
                    }

                    val intent = Intent(this@CameraActivity, PreviewActivity::class.java)
                    intent.putStringArrayListExtra(PreviewActivity.EXTRA_PHOTO_RESULT, ArrayList(filePaths))
                    intent.putExtra(
                        PreviewActivity.EXTRA_CAMERA_MODE,
                        cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
                    startActivity(intent)
                    this@CameraActivity.finish()
                } else {
                    // Handle the case where no image is selected from the gallery
                    // For example, show an error message or perform any necessary action
                }
            }
        }
    }



    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        openGalleryLauncher.launch(intent)
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let { file ->
            File(file, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return mediaDir ?: filesDir
    }
    companion object {
        private const val TAG = "CameraActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val REQUEST_CODE = 200
    }

}