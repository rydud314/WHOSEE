package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityEditScheduleBinding

class EditSchedule : AppCompatActivity(){
    private lateinit var binding: ActivityEditScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventBundle = intent.getBundleExtra("eventBundle")

        val eventTitle = eventBundle?.getString("eventTitle")
        val eventStartTime = eventBundle?.getString("eventStartTime")
        val eventEndTime = eventBundle?.getString("eventEndTime")
        val eventDescription = eventBundle?.getString("eventDescription")

        //val eventStartDate = bundle?.getString("eventStartDate")
        //val eventEndDate = bundle?.getString("eventEndDate")





    }

}