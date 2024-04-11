package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CameraScan_1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_scan_1)

        val btn_scan: Button = findViewById(R.id.btn_scan)
        btn_scan.setOnClickListener{
            val intent = Intent(this, CameraScan_2::class.java)
            startActivity(intent)
            finish()
        }
    }
}
