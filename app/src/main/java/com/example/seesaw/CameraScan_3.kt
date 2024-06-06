package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CameraScan_3 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_scan_3)

        val btn_add_more: Button = findViewById(R.id.btn_add_more)
        btn_add_more.setOnClickListener{
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btn_finish: Button = findViewById(R.id.btn_finish)
        btn_finish.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}