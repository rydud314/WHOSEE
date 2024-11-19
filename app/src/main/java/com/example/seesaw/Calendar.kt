package com.example.seesaw
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.seesaw.databinding.ActivityCalendarBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.DateTime
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.CalendarScopes
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.Events
import com.prolificinteractive.materialcalendarview.CalendarDay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Calendar : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarBinding
    private lateinit var googleCalendarEvents: List<com.google.api.services.calendar.model.Event>

    private val RC_SIGN_IN = 100  // 요청 코드 정의

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d(ContentValues.TAG, "캘린더 = 입장")

        binding.selectedDateRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.mcCalendar

        // GoogleSignInOptions 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(CalendarScopes.CALENDAR))
            .requestScopes(Scope("https://www.googleapis.com/auth/calendar")) // Calendar API에 필요한 스코프 요청
            .build()

        Log.d(ContentValues.TAG, "캘린더 = 1")

        // GoogleSignInClient 생성
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        Log.d(ContentValues.TAG, "캘린더 = 1-2")


        // Google 로그인 시작
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)


        binding.btnAddSchedule.setOnClickListener {
            Log.d(ContentValues.TAG, "캘린더 = 일정추가버튼")

            val intent = Intent(this, AddSchedule::class.java)
            startActivity(intent)
            finish()
        }

        val today = CalendarDay.today()
        binding.mcCalendar.addDecorator(TodayDecorator(this, today))

        binding.mcCalendar.setSelectedDate(today)

        binding.mcCalendar.setOnDateChangedListener { widget, date, selected ->
            if (::googleCalendarEvents.isInitialized && googleCalendarEvents.isNotEmpty()) {
                // 클릭한 날짜를 Date 객체로 변환
                val selectedDate = java.util.Calendar.getInstance().apply {
                    set(date.year, date.month-1, date.day) // 연도, 월, 일 설정
                }.time
                //선택한 날짜 일정 찾기
                val filteredEvents = googleCalendarEvents.filter { event ->
                    val startDate = event.start.dateTime ?: event.start.date
                    val eventsDate = Date(startDate.value)
                    var endDate = event.end.dateTime ?: event.end.date

                    if(event.end.dateTime == null){
                        //date형 변수일 때 일자가 하루씩 늘어나는 현상을 줄이는 코드
                        val c = java.util.Calendar.getInstance()
                        c.time = Date(endDate.value)
                        c.add(java.util.Calendar.DAY_OF_MONTH, -1)
                        endDate = DateTime(c.time)

                    }else{
                        //endDate = event.end.dateTime
                    }
                    val eventeDate = Date(endDate.value)
                    isSameDay(selectedDate, eventsDate, eventeDate)
                }
                displaySelectedDateEvents(filteredEvents)
            }else{
                Log.d(ContentValues.TAG,"캘린더 필터링된 이벤트가 없음")
            }
        }

        binding.mcCalendar.setOnMonthChangedListener { widget, date ->
            widget.clearSelection()
            // 클릭한 날짜를 Date 객체로 변환
            val firstDayOfMonth = CalendarDay.from(date.year, date.month, 1)

            val selectedDate = java.util.Calendar.getInstance().apply {
                set(
                    firstDayOfMonth.year,
                    firstDayOfMonth.month - 1,
                    firstDayOfMonth.day
                ) // 연도, 월, 일 설정
            }.time
            widget.setDateSelected(firstDayOfMonth, true)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    loadEvents(selectedDate)
                }catch (e : Exception){
                    e.printStackTrace()
                    // 오류 처리
                    Log.d(ContentValues.TAG, "캘린더 loadEvent : ${e.message}")
                }
            }
        }
     }

    // Google Sign-In 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(ContentValues.TAG, "캘린더 로그인: ${account?.email}")
                handleSignInResult(account)  // 로그인 성공 시 처리
            } catch (e: ApiException) {
                Log.e(ContentValues.TAG, "캘린더 로그인 실패: ${e.statusCode} - ${e.message}")
                e.printStackTrace()  // 로그인 실패 처리
            }
        }
    }

    private fun handleSignInResult(account: GoogleSignInAccount?) {
        if (account != null) {
            // GoogleSignInAccount 객체를 사용해 API 요청에 필요한 자격 증명을 생성
            val credential = GoogleAccountCredential.usingOAuth2(
                this, listOf("https://www.googleapis.com/auth/calendar")
            )
            credential.selectedAccount = account.account
            Log.d(ContentValues.TAG, "캘린더 3 : ${credential}")
            // Google Calendar API 호출
            accessGoogleCalendar(credential)
        }
        else{
            Log.d(ContentValues.TAG, "캘린더 3 : account is null")
        }
    }

    // Google Calendar API 호출
    private fun accessGoogleCalendar(credential: GoogleAccountCredential) {
        val service = Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
            .setApplicationName("Android WHOSEE Client")
            .build()
        Log.d(ContentValues.TAG, "캘린더 4")

        CalendarServiceSingleton.calendarService = service

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val now = Date(System.currentTimeMillis())
                loadEvents(now)
            } catch (e: Exception) {
                e.printStackTrace()
                // 오류 처리
                Log.d(ContentValues.TAG, "캘린더 loadEvent : ${e.message}")
            }
        }
    }

    private suspend fun loadEvents(calDate: Date){

        val firstDayOfMonth = getStartOfMonth(calDate)
        val lastDayOfMonth = getEndOfMonth(calDate)
        val service = CalendarServiceSingleton.calendarService

        Log.d(ContentValues.TAG, "캘린더 API 요청 시작")

        service?.let {
            val events: Events = service.events().list("primary")
                .setMaxResults(100)
                .setTimeMin(firstDayOfMonth)
                .setTimeMax(lastDayOfMonth)
                .setTimeZone("Asia/Seoul")
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute()
            Log.d(ContentValues.TAG, "캘린더 API 호출 성공 : ${events.items}")

            googleCalendarEvents = emptyList()
            googleCalendarEvents=events.items //수상함


            // UI 업데이트는 메인 스레드에서 수행해야 함
            withContext(Dispatchers.Main) {
                // 이벤트 처리
                //Log.d(ContentValues.TAG, "캘린더 4-2 : ${events.items}")
                val eventList=googleCalendarEvents
                if (eventList.isNullOrEmpty()) {
                    Log.d(ContentValues.TAG, "캘린더 이벤트 없음")
                    displaySelectedDateEvents(eventList)
                } else {
                    val calendarDayList = getEventDates(eventList)
                    binding.mcCalendar.addDecorator(EventDecorator(this@Calendar, calendarDayList))

                    val filteredEvents = googleCalendarEvents.filter { event ->
                        val startDate = event.start.dateTime ?: event.start.date
                        val eventsDate = Date(startDate.value)
                        var endDate = event.end.dateTime ?: event.end.date

                        if(event.end.dateTime == null){
                            //date형 변수일 때 일자가 하루씩 늘어나는 현상을 줄이는 코드
                            val c = java.util.Calendar.getInstance()
                            c.time = Date(endDate.value)
                            c.add(java.util.Calendar.DAY_OF_MONTH, -1)
                            endDate = DateTime(c.time)
                        }else{
                            //endDate = event.end.dateTime
                        }
                        val eventeDate = Date(endDate.value)
                        isSameDay(calDate, eventsDate, eventeDate)
                    }
                    Log.d(ContentValues.TAG, "캘린더 4-2 : ListView 업데이트 완료")
                    displaySelectedDateEvents(filteredEvents)
                }
            }
        } ?: run {
            // calendarService가 null일 경우 이 블록이 실행됩니다.
            Log.d(TAG, "캘린더 service null")
        }

    }


    private fun displaySelectedDateEvents(events: List<com.google.api.services.calendar.model.Event>) {
        if (events.isEmpty()) {
            binding.noEventMsg.visibility = View.VISIBLE
            binding.selectedDateRecyclerView.visibility = View.GONE
        } else {
            binding.noEventMsg.visibility = View.GONE
            binding.selectedDateRecyclerView.visibility = View.VISIBLE

            val account = GoogleSignIn.getLastSignedInAccount(this)

            val selectedDateEventAdapter = account?.let { EventAdapter(events, this) }
            binding.selectedDateRecyclerView.adapter = selectedDateEventAdapter
        }
    }

    private fun isSameDay(dateToCheck: Date, startDate: Date, endDate: Date): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDateToCheck = format.format(dateToCheck)
        val formattedStartDate = format.format(startDate)
        val formattedEndDate = format.format(endDate)

        // 날짜가 시작일과 끝일 사이에 포함되는지 확인
        return formattedDateToCheck >= formattedStartDate && formattedDateToCheck <= formattedEndDate
    }
    fun getStartOfMonth(calDate :Date): DateTime {
        val calendar =  java.util.Calendar.getInstance() // 현재 날짜와 시간으로 Calendar 객체 생성
        calendar.time = calDate
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1) // 해당 달의 첫 번째 날짜로 설정
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        return DateTime(calendar.time) // Calendar의 Date 객체로 DateTime 생성
    }
    fun getEndOfMonth(calDate: Date): DateTime {
        val calendar = java.util.Calendar.getInstance()
        calendar.time = calDate

        val lastDay = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)

        calendar.set(java.util.Calendar.DAY_OF_MONTH, lastDay) // 해당 달의 첫 번째 날짜로 설정
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 23)
        calendar.set(java.util.Calendar.MINUTE, 59)
        calendar.set(java.util.Calendar.SECOND, 59)
        calendar.set(java.util.Calendar.MILLISECOND, 999)

        return DateTime(calendar.time)
    }

    fun getEventDates(eventList: List<Event>): List<CalendarDay> {
        var eventDates = mutableListOf<CalendarDay>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Event 리스트에서 날짜를 추출하여 CalendarDay 리스트로 변환
        for (event in eventList) {
            val startDate = event.start.dateTime ?: event.start.date
            val eventsDate = Date(startDate.value)
            var endDate = event.end.dateTime ?: event.end.date

            if(event.end.dateTime == null){
                //date형 변수일 때 일자가 하루씩 늘어나는 현상을 줄이는 코드
                val c = java.util.Calendar.getInstance()
                c.time = Date(endDate.value)
                c.add(java.util.Calendar.DAY_OF_MONTH, -1)
                endDate = DateTime(c.time)
            }else{
                //endDate = event.end.dateTime
            }
            val eventeDate = Date(endDate.value)
            val calendar = java.util.Calendar.getInstance()
            var pick = eventsDate

            while (isSameDay(pick, eventsDate, eventeDate)){
                calendar.time = pick
                eventDates.add(CalendarDay.from(calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH)+1, calendar.get(java.util.Calendar.DAY_OF_MONTH)))

                calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)  // 하루를 더함
                pick = calendar.time
            }

        }
        return eventDates
    }
    companion object {
        private const val CREDENTIALS_FILE_PATH = "credentials.json"
        private const val TOKENS_DIRECTORY_PATH = "tokens"
        private val SCOPES = listOf(CalendarScopes.CALENDAR_READONLY)
        private const val APPLICATION_NAME = "Google Calendar API Example"
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
        private val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    }

    object CalendarServiceSingleton {
        var calendarService: Calendar? = null
    }
}