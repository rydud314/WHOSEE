package com.example.seesaw

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityEditScheduleBinding
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditSchedule : AppCompatActivity(){
    private lateinit var binding: ActivityEditScheduleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var scheduleMap = mutableMapOf<String, String?>()
        val eventBundle = intent.getBundleExtra("eventBundle")

        //calendarService 받아오기
        val calendarService = Calendar.CalendarServiceSingleton.calendarService
        if (calendarService == null) {
            Log.d(TAG, "캘린더(일정수정) : calendarService is null")
            return
        }

        val eventId = eventBundle?.getString("eventId")
        val eventTitle = eventBundle?.getString("eventTitle")
        binding.etEventTitle.setText(eventTitle)


        val eventStartTime = eventBundle?.getString("eventStartTime")?.trim()
        val eventEndTime = eventBundle?.getString("eventEndTime")?.trim()
        val bsd = eventStartTime?.substring(0, 10)
        val bst = eventStartTime?.substring(11)
        val bed = eventEndTime?.substring(0, 10)
        val bet = eventEndTime?.substring(11)

        binding.etStartDate.setText(bsd)
        binding.spinnerStartTime
        binding.etEndDate.setText(bed)
        binding.spinnerEndTime

        if (bst == "00:00" && bet == "23:59"){
            binding.checkBoxEventAllDay.isChecked = true
            binding.spinnerStartTime.isEnabled = false
            binding.spinnerEndTime.isEnabled = false

            binding.spinnerStartTime.alpha = 0.5f
            binding.spinnerEndTime.alpha = 0.5f
        }
        else{
            binding.checkBoxEventAllDay.isChecked = false
        }

        val eventDescription = eventBundle?.getString("eventDescription")
        binding.etDescription.setText(eventDescription)

        val timeoptions=(0..23).flatMap { hour->
            listOf(String.format("%02d:00",hour),String.format("%02d:30",hour))
        }
        val adapter= ArrayAdapter(this,android.R.layout.simple_spinner_item,timeoptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStartTime.adapter=adapter
        binding.spinnerEndTime.adapter=adapter

        binding.spinnerStartTime.setSelection(timeoptions.indexOf(bst))
        binding.spinnerEndTime.setSelection(timeoptions.indexOf(bet))

        binding.etStartDate.setOnClickListener{
            showDatePicker{date->binding.etStartDate.setText(date)}
        }
        binding.etEndDate.setOnClickListener{
            showDatePicker{date->binding.etEndDate.setText(date)}
        }

        binding.checkBoxEventAllDay.setOnCheckedChangeListener { _, isChecked ->
            binding.spinnerStartTime.isEnabled = !isChecked
            binding.spinnerEndTime.isEnabled = !isChecked

            binding.spinnerStartTime.alpha = if (isChecked) 0.5f else 1.0f // 비활성화 시 투명도 변경
            binding.spinnerEndTime.alpha = if (isChecked) 0.5f else 1.0f

            if(isChecked){
                binding.spinnerStartTime.setSelection(timeoptions.indexOf("00:00"))
                binding.spinnerEndTime.setSelection(timeoptions.indexOf("00:00"))
            }
        }


        binding.btnSaveSchedule.setOnClickListener{
            Log.d(TAG, "캘린더(일정수정) = Edit 버튼 클릭")

            var title = binding.etEventTitle.text.toString()
            var startDate = binding.etStartDate.text.toString()
            var endDate = binding.etEndDate.text.toString()
            var startTime= binding.spinnerStartTime.selectedItem.toString()
            var endTime=binding.spinnerEndTime.selectedItem.toString()
            var checkAllDay = binding.checkBoxEventAllDay


            if (title != "" && startDate != "" && endDate != ""){

                scheduleMap["eventId"] = eventId
                scheduleMap["eventTitle"] = title
                scheduleMap["startTime"] =startTime
                scheduleMap["startDate"] = startDate
                scheduleMap["endDate"] = endDate
                scheduleMap["endTime"] = endTime
                scheduleMap["description"] = binding.etDescription.text.toString()

                if(checkAllDay.isChecked){ scheduleMap["allDay"] = "true"}
                else{scheduleMap["allDay"] = "false"}

                editEvent(calendarService, "primary", scheduleMap)
            }else{
                Toast.makeText(this, "이벤트 이름과 시작/종료 날짜는 필수 기입 항목입니다.", Toast.LENGTH_SHORT).show()
            }
        }



    }
    private fun editEvent(calendarService: com.google.api.services.calendar.Calendar, calendarId: String, map: MutableMap<String, String?>) {

        val eventId = map["eventId"]

        val startDate = map["startDate"]
        var startTime = map["startTime"]
        val endDate = map["endDate"]
        var endTime = map["endTime"]
        var allDay = map["allDay"]

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val event = calendarService.events().get("primary", eventId).execute()
                Log.d(TAG, "캘린더(일정수정) : event 다시 불러오기 ${event.summary}")

                event.summary = map["eventTitle"]
                event.description = map["description"]

                if (allDay == "true") {
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

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val eventResult =
                            calendarService.events().update("primary", eventId, event).execute()

                        // 이벤트 추가가 성공하면 Main 스레드에서 Intent 실행
                        withContext(Dispatchers.Main) {
                            Log.d(TAG, "캘린더(일정수정)Event modified: ${eventResult.htmlLink}")
                            Toast.makeText(this@EditSchedule, "일정이 변경되었습니다.", Toast.LENGTH_SHORT)
                                .show()
                            val intent =
                                Intent(this@EditSchedule, com.example.seesaw.Calendar::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            startActivity(intent)
                            finish()
                        }
                    } catch (e: Exception) {
                        // 에러 처리
                        Log.e(TAG, "캘린더(일정수정): 변경 실패: ${e.message}")

                    }
                }
            } catch (e:Exception){
                Log.e(TAG, "캘린더(일정수정): event get 실패: ${e.message}")

            }
        }

""    }


    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val month = calendar.get(java.util.Calendar.MONTH)
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
            onDateSelected(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }

}