package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityDeleteScheduleBinding

class EventDetail : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventBundle = intent.getBundleExtra("eventBundle")

        val eventTitle = eventBundle?.getString("eventTitle")
        //val eventStartDate = bundle?.getString("eventStartDate")
        val eventStartTime = eventBundle?.getString("eventStartTime")
        //val eventEndDate = bundle?.getString("eventEndDate")
        val eventEndTime = eventBundle?.getString("eventEndTime")
        val eventDescription = eventBundle?.getString("eventDescription")

        binding.tvEventName.text = eventTitle
        binding.tvStartDate.text = eventStartTime
        binding.tvEndDate.text = eventEndTime
        binding.tvDescription.text = eventDescription

        binding.btnEditSchedule.setOnClickListener {
            val intent = Intent(this, EditSchedule::class.java)
            intent.putExtra("eventBundle", eventBundle)
            startActivity(intent)
            finish()
        }

        binding.btnDeleteSchedule.setOnClickListener {


        }



    }
}