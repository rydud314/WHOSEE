package com.example.seesaw

import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityEditScheduleBinding

class EditSchedule : AppCompatActivity(){
    private lateinit var binding: ActivityEditScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventBundle = intent.getBundleExtra("eventBundle")

        //calendarService 받아오기
        val calendarService = Calendar.CalendarServiceSingleton.calendarService
        if (calendarService == null) {
            Log.d(TAG, "캘린더(일정삭제) : calendarService is null")
            return
        }

        val eventTitle = eventBundle?.getString("eventTitle")
        val eventStartTime = eventBundle?.getString("eventStartTime")
        val eventEndTime = eventBundle?.getString("eventEndTime")
        val eventDescription = eventBundle?.getString("eventDescription")

        //val eventStartDate = bundle?.getString("eventStartDate")
        //val eventEndDate = bundle?.getString("eventEndDate")





    }

}