package com.example.seesaw

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityAddScheduleBinding
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar as Calendar2
import android.app.DatePickerDialog

class AddSchedule : AppCompatActivity() {

    private lateinit var binding: ActivityAddScheduleBinding
    private val dateFormat=SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(ContentValues.TAG, "캘린더 스케줄 추가 = 입장")

        var scheduleMap = mutableMapOf<String, String>()

        //calendarService 받아오기
        val calendarService = com.example.seesaw.Calendar.CalendarServiceSingleton.calendarService
        if (calendarService == null) {
            Log.d(TAG, "캘린더(일정추가) : calendarService is null")
            return
        }

        binding.etStartDate.setOnClickListener{
            showDatePicker{date->binding.etStartDate.setText(date)}
        }
        binding.etEndDate.setOnClickListener{
            showDatePicker{date->binding.etEndDate.setText(date)}
        }

        val timeoptions=(0..23).flatMap { hour->
            listOf(String.format("%02d:00",hour),String.format("%02d:30",hour))
        }
        val adapter=ArrayAdapter(this,android.R.layout.simple_spinner_item,timeoptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStartTime.adapter=adapter
        binding.spinnerEndTime.adapter=adapter

        binding.btnAddSchedule.setOnClickListener{
            Log.d(TAG, "캘린더(일정추가) = add버튼 클릭")

            var title = binding.etEventTitle.text.toString()
            var startDate = binding.etStartDate.text.toString()
            var endDate = binding.etEndDate.text.toString()
            var startTime= binding.spinnerStartTime.selectedItem.toString()
            var endTime=binding.spinnerEndTime.selectedItem.toString()
            var checkAllDay = binding.checkBoxEventAllDay


            if (title != "" && startDate != "" && endDate != ""){
                startDate = "${startDate.substring(0, 4)}-${startDate.substring(4, 6)}-${startDate.substring(6, 8)}"
                endDate = "${endDate.substring(0, 4)}-${endDate.substring(4, 6)}-${endDate.substring(6, 8)}"

                scheduleMap["eventTitle"] = title
                scheduleMap["startTime"] =startTime
                scheduleMap["startDate"] = startDate
                scheduleMap["endDate"] = endDate
                scheduleMap["endTime"] = endTime
                scheduleMap["description"] = binding.etDescription.text.toString()

                val email = binding.etParticipant.text.toString().trim()
                if(email != ""){
                    val emailDomain = binding.spinnerEmailDomains.selectedItem.toString()
                    scheduleMap["participant"] = email + emailDomain
                }
                else{
                    scheduleMap["participant"] = email
                }
                if(checkAllDay.isChecked){ scheduleMap["allDay"] = "true"}
                else{scheduleMap["allDay"] = "false"}

                addEvent(calendarService, scheduleMap)
            }else{
                Toast.makeText(this, "이벤트 이름과 시작/종료 날짜는 필수 기입 항목입니다.", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar2.getInstance()
        val year = calendar.get(Calendar2.YEAR)
        val month = calendar.get(Calendar2.MONTH)
        val day = calendar.get(Calendar2.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d%02d%02d", selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun addEvent(calendarService: com.google.api.services.calendar.Calendar, map: MutableMap<String, String>) {
        val scheduleMap = map
        val event = Event()
            .setSummary(scheduleMap["eventTitle"])
            .setDescription(scheduleMap["description"])


        /*
        // 시작 및 종료 시간 설정
        val startDateTime = DateTime("2024-11-10T10:00:00+09:00")
        event.start = EventDateTime().setDateTime(startDateTime).setTimeZone("Asia/Seoul")

        val endDateTime = DateTime("2024-11-10T11:00:00+09:00")
        event.end = EventDateTime().setDateTime(endDateTime).setTimeZone("Asia/Seoul")

         */

        //들어가야하는 형식 : "2024-11-10T10:00:00+09:00"

        val startDate = scheduleMap["startDate"]
        var startTime =scheduleMap["startTime"]
        val endDate = scheduleMap["endDate"]
        var endTime = scheduleMap["endTime"]
        var allDay = scheduleMap["allDay"]


        if (startTime == "" && endTime==""){
            val sDate = DateTime("$startDate + T + 00:00:00+09:00")
            val start = EventDateTime()
                .setDateTime(sDate)
                .setTimeZone("Asia/Seoul")
            event.start = start
            val eDate = DateTime("$endDate+T+23:59:00+09:00")
            val end = EventDateTime()
                .setDateTime(eDate)
                .setTimeZone("Asia/Seoul")
            event.end = end

//            var sDate = DateTime(startDate)
//            val start = EventDateTime()
//                .setDate(sDate)
//                .setTimeZone("Asia/Seoul")
//            event.start = start
//
//            var eDate = DateTime(endDate)
//            val end = EventDateTime()
//                .setDate(eDate)
//                .setTimeZone("Asia/Seoul")
//            event.end = end
        }
        else{
            if(allDay == "true") {
                startTime = "00:00"
                endTime = "23:59"
            }

            val startDateTime = DateTime(startDate + "T" + startTime + ":00+09:00")
            val start = EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Asia/Seoul")
            event.start = start

            val endDateTime = DateTime(endDate + "T" + endTime + ":00+09:00")

            val end = EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Asia/Seoul")
            event.end = end
        }
        /*
        // 참여자 추가 (이메일 포맷 확인하는 조건문 필요)
        val attendee = scheduleMap["participant"]
        if(attendee != ""){
            val attendees = listOf(
                EventAttendee().setEmail("a01099470314@gmail.com")
            )
            event.attendees = attendees
        }

         */

        CoroutineScope(Dispatchers.IO).launch {
            try {

                val eventResult = calendarService.events().insert("primary", event).execute()

                // 이벤트 추가가 성공하면 Main 스레드에서 Intent 실행
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "캘린더(일정추가)Event created: ${eventResult.htmlLink}")
                    Toast.makeText(this@AddSchedule, "일정을 추가하였습니다.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@AddSchedule, com.example.seesaw.Calendar::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                }
            } catch (e: Exception) {
                // 에러 처리
                Log.e(TAG, "캘린더(일정추가): 실패: ${e.message}")

            }
        }

    }
}