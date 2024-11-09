package com.example.seesaw
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.google.api.services.calendar.model.Events
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar as UtilCalendar

class Calendar : AppCompatActivity() {

    private lateinit var btn_add_schedule : Button
    private lateinit var listView: ListView
    private lateinit var selectedDateListView: ListView
    private lateinit var calendar: CalendarView
    private lateinit var googleCalendarEvents: List<com.google.api.services.calendar.model.Event>
    private lateinit var noEventmsg: TextView

    private val RC_SIGN_IN = 100  // 요청 코드 정의

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        Log.d(ContentValues.TAG, "캘린더 = 입장")

        btn_add_schedule = findViewById(R.id.btn_add_schedule)
        listView = findViewById(R.id.calendarListView)
        selectedDateListView=findViewById(R.id.selectedDateListView)
        calendar=findViewById(R.id.calendar)
        noEventmsg= findViewById(R.id.noEventMsg)

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


        btn_add_schedule.setOnClickListener {
            Log.d(ContentValues.TAG, "캘린더 = 일정추가버튼")

            val account = GoogleSignIn.getLastSignedInAccount(this)
            Log.d(ContentValues.TAG, "캘린더 account = ${account}")
            val intent = Intent(this, AddSchedule::class.java)
            intent.putExtra("calendarAccount", account)
            startActivity(intent)
        }

        calendar.setOnDateChangeListener { _, year, month, dayOfMonth ->
            Log.d(ContentValues.TAG,"날짜 선택")

             if (::googleCalendarEvents.isInitialized && googleCalendarEvents.isNotEmpty()) {
                val selectedDate = UtilCalendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }.time

                //선택한 날짜 일정 찾기
                val filteredEvents = googleCalendarEvents.filter { event ->
                    val startDate = event.start.dateTime ?: event.start.date
                    val eventDate = Date(startDate.value)
                    isSameDay(selectedDate, eventDate)
                }
                 displaySelectedDateEvents(filteredEvents)

//                 if (filteredEvents.isEmpty()) {
//                     Log.d(ContentValues.TAG, "선택한 날짜에 일정 없음")
//                 } else {
//                     displaySelectedDateEvents(filteredEvents)
//                 }

            }else{
                Log.d(ContentValues.TAG,"캘린더 이벤트가 없음")
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

        val now = DateTime(System.currentTimeMillis())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(ContentValues.TAG, "캘린더 API 요청 시작")
                val events: Events = service.events().list("primary")
                    .setMaxResults(100)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
                Log.d(ContentValues.TAG, "캘린더 API 호출 성공 : ${events.items}")

                val eventTitles = events.items.map { it.summary ?: "No Title" }
                googleCalendarEvents=events.items //수상함

                // UI 업데이트는 메인 스레드에서 수행해야 함
                withContext(Dispatchers.Main) {
                    // 이벤트 처리
                    //Log.d(ContentValues.TAG, "캘린더 4-2 : ${events.items}")
                    val eventList=googleCalendarEvents
                    if (eventList.isNullOrEmpty()) {
                        Log.d(ContentValues.TAG, "캘린더 이벤트 없음")
                    } else {
                        displayEvents(eventList)
                        Log.d(ContentValues.TAG, "캘린더 4-2 : ListView 업데이트 완료")
                        displaySelectedDateEvents(eventList)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 오류 처리
                Log.d(ContentValues.TAG, "캘린더 4-3 : ${e.message}")

            }
        }
    }


    private fun displayEvents(events: List<com.google.api.services.calendar.model.Event>) {
        //리스트뷰 어댑터 연결
        val eventAdapter =EventAdapter(this,events)
        listView.adapter=eventAdapter

        listView.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = event
            Toast.makeText(this, "$selectedItem clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun displaySelectedDateEvents(events: List<com.google.api.services.calendar.model.Event>) {
        if (events.isEmpty()) {
            noEventmsg.visibility = View.VISIBLE
            selectedDateListView.visibility = View.GONE
        } else {
            noEventmsg.visibility = View.GONE
            selectedDateListView.visibility = View.VISIBLE

            val selectedDateEventAdapter = EventAdapter(this, events)
            selectedDateListView.adapter = selectedDateEventAdapter
        }
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fdate1= format.format(date1)
        val fdate2=format.format(date2)

        return fdate1==fdate2
    }

    companion object {
        private const val CREDENTIALS_FILE_PATH = "credentials.json"
        private const val TOKENS_DIRECTORY_PATH = "tokens"
        private val SCOPES = listOf(CalendarScopes.CALENDAR_READONLY)
        private const val APPLICATION_NAME = "Google Calendar API Example"
        private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
        private val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
    }
}