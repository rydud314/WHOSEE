package com.example.seesaw

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CameraScan_2 : AppCompatActivity(){

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_scan_2)

        val btn_close: Button = findViewById(R.id.btn_close)
        btn_close.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btn_retake: Button = findViewById(R.id.btn_retake)
        btn_retake.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btn_save: Button = findViewById(R.id.btn_save)
        btn_save.setOnClickListener{
            val intent = Intent(this, CameraScan_3::class.java)
            startActivity(intent)
            finish()
        }

    }
}