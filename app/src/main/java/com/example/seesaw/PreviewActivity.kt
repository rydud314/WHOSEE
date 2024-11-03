package com.example.seesaw

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class PreviewActivity : AppCompatActivity() {

    private lateinit var imageViewPreview: ImageView
    private lateinit var btnRetake: Button
    private lateinit var btnSave: Button
    private var savedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        imageViewPreview = findViewById(R.id.imageViewPreview)
        btnRetake = findViewById(R.id.btnRetake)
        btnSave = findViewById(R.id.btnSave)

        savedUri = Uri.parse(intent.getStringExtra("photoUri"))
        imageViewPreview.setImageURI(savedUri)

        btnRetake.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSave.setOnClickListener {
            savePhotoToGallery()
        }
    }

    private fun savePhotoToGallery() {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()))
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Camera")
            }
        }

        savedUri?.let { uri ->
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let {
                contentResolver.openOutputStream(it).use { outputStream ->
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.copyTo(outputStream!!)
                    }
                }
            }
            Toast.makeText(this, "Photo saved to gallery", Toast.LENGTH_SHORT).show()
        }
    }
}
