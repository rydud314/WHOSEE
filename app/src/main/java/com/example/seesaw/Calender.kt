package com.example.seesaw

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Calender : AppCompatActivity() {

    private lateinit var btn_add_schedule : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        Log.d(ContentValues.TAG, "캘린더 = 입장")


        btn_add_schedule = findViewById(R.id.btn_add_schedule)
        btn_add_schedule.setOnClickListener {
            val intent = Intent(this, AddSchedule::class.java)
            startActivity(intent)
        }
    }
}