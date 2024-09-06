package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Calender : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val btn_add_schedule: Button = findViewById(R.id.btn_add_schedule)
        btn_add_schedule.setOnClickListener {
            val intent = Intent(this, Add_Schedule::class.java)
            startActivity(intent)
        }
    }
}