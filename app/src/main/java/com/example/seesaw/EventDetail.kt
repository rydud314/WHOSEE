package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityDeleteScheduleBinding

class EventDetail : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)



    }
}