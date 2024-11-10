package com.example.seesaw

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityDeleteScheduleBinding
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EventDetail : AppCompatActivity() {
    private lateinit var binding: ActivityDeleteScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeleteScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val eventBundle = intent.getBundleExtra("eventBundle")

        //calendarService 받아오기
        val calendarService = Calendar.CalendarServiceSingleton.calendarService
        if (calendarService == null) {
            Log.d(TAG, "캘린더(일정삭제) : calendarService is null")
            return
        }
        Log.d(TAG, "캘린더(일정삭제) : calendarService = ${calendarService.toString()}")
        
        val eventId = eventBundle?.getString("eventId")
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
            val dialogView = layoutInflater.inflate(R.layout.dialog_event_delete, null)


            val dialog = AlertDialog.Builder(this)
                .setTitle("일정을 삭제하시겠습니까?")
                .setView(dialogView)
                .setPositiveButton("확인") { _, _ ->
                    if (eventId != null) {
                        Log.d(TAG, "Event deleted successfully.")
                        deleteEvent(calendarService, "primary", eventId)
                    }
                }
                .setNegativeButton("취소", null)
                .create()

            dialog.show()

        }
    }

    private fun deleteEvent(calendarService: com.google.api.services.calendar.Calendar, calendarId: String, eventId: String) {

        CoroutineScope(Dispatchers.IO).launch {
            try {
                calendarService.events().delete(calendarId, eventId).execute()

                // 이벤트 삭제 성공하면 Main 스레드에서 Intent 실행
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "캘린더(일정삭제) 삭제 성공")
                    Toast.makeText(this@EventDetail, "일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@EventDetail, com.example.seesaw.Calendar::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d(TAG, "캘린더(일정삭제) 삭제 실패: ${e.message}")
            }
        }
    }

}