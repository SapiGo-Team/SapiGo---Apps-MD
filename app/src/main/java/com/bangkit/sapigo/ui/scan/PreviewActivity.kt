package com.bangkit.sapigo.ui.scan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.sapigo.adapter.ImageAdapter
import com.bangkit.sapigo.data.viewmodel.PreviewViewModel
import com.bangkit.sapigo.databinding.ActivityPreviewBinding
import com.bangkit.sapigo.utils.Helpers
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewBinding
    private lateinit var sharedPreferences: SharedPreferences
    private var selectedImagePath: String = ""
    private var compiledImage: Bitmap? = null

    private val viewModel: PreviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePaths = intent?.getStringArrayListExtra(EXTRA_PHOTO_RESULT) ?: ArrayList()
        val isBackCamera = intent?.getBooleanExtra(EXTRA_CAMERA_MODE, true) ?: true

        val imageList = ArrayList<Bitmap>()

        if (filePaths.isNotEmpty()) {
            for (filePath in filePaths) {
                val file = File(filePath)
                if (file.exists()) {
                    val rotatedBitmap = Helpers.rotateBitmap(
                        BitmapFactory.decodeFile(file.absolutePath),
                        isBackCamera
                    )
                    imageList.add(rotatedBitmap)

                    if (selectedImagePath.isEmpty()) {
                        selectedImagePath = file.absolutePath // Store the path of the first image
                    }
                }
            }
        }

        val adapter = ImageAdapter(imageList)
        binding.recyclerView.layoutManager =
            GridLayoutManager(this, 4, RecyclerView.HORIZONTAL, false)
        binding.recyclerView.adapter = adapter

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnConfirm.setOnClickListener {
            if (selectedImagePath.isNotEmpty()) {
                val imageFiles = ArrayList<File>()
                for (bitmap in imageList) {
                    val file = createImageFile()
                    val stream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                    stream.flush()
                    stream.close()
                    imageFiles.add(file)
                }
                uploadImages(imageFiles)
                compileImagesAndUpload(imageFiles)
            }
        }

        sharedPreferences = getSharedPreferences("My_prefs", Context.MODE_PRIVATE)
    }

    private fun uploadImages(images: List<File>) {
        viewModel.uploadImages(images, this)
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFileName = "JPEG_${timeStamp}_"
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    private fun compileImagesAndUpload(images: List<File>) {
        val compiledImageUri = compileImages(images)

        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("COMPILED_IMAGE_URI", compiledImageUri)
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent)
    }

    private fun compileImages(images: List<File>): Uri? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        BitmapFactory.decodeFile(images[0].absolutePath, options)
        val width = options.outWidth
        val height = options.outHeight

        val compositeBitmap = Bitmap.createBitmap(width * 2, height * 2, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(compositeBitmap)

        for (i in images.indices) {
            val imageBitmap = BitmapFactory.decodeFile(images[i].absolutePath)
            val x = (i % 2) * width
            val y = (i / 2) * height
            canvas.drawBitmap(imageBitmap, x.toFloat(), y.toFloat(), null)
        }

        // Save the compiled image to a file
        val compositeImageFile = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "compiled_image.jpg")
        try {
            val outputStream = FileOutputStream(compositeImageFile)
            compositeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return if (compositeImageFile.exists()) Uri.fromFile(compositeImageFile) else null
    }



    companion object {
        const val EXTRA_PHOTO_RESULT = "PHOTO_RESULT"
        const val EXTRA_CAMERA_MODE = "CAMERA_MODE"
    }
}




