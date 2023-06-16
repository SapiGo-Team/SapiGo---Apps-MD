package com.bangkit.sapigo.data

import android.graphics.Bitmap
import android.net.Uri


data class ResultData(
    val id: Long, // Unique identifier for each entry
    val imageUri: Uri,
    val responses: List<String>
)