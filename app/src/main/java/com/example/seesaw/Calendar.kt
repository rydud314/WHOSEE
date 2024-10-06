package com.example.seesaw
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
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


class Calendar : AppCompatActivity() {

    private lateinit var btn_add_schedule : Button


    private val RC_SIGN_IN = 100  // 요청 코드 정의

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        Log.d(ContentValues.TAG, "캘린더 = 입장")

        btn_add_schedule = findViewById(R.id.btn_add_schedule)

        // GoogleSignInOptions 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope("https://www.googleapis.com/auth/calendar")) // Calendar API에 필요한 스코프 요청
            .build()

        Log.d(ContentValues.TAG, "캘린더 = 1")

        // GoogleSignInClient 생성
        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        Log.d(ContentValues.TAG, "캘린더 = 1-2")


        // Google 로그인 시작
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    // Google Sign-In 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d(ContentValues.TAG, "캘린더 로그인: ${account}")
                handleSignInResult(account)  // 로그인 성공 시 처리
            } catch (e: ApiException) {
                Log.d(ContentValues.TAG, "캘린더 로그인 : 로그인 실패")
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
                val events: Events = service.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute()
                Log.d(ContentValues.TAG, "캘린더 4-2 : ${events.items}")

                // UI 업데이트는 메인 스레드에서 수행해야 함
                withContext(Dispatchers.Main) {
                    // 이벤트 처리
                    Log.d(ContentValues.TAG, "캘린더 4-2 : ${events.items}")

                }
            } catch (e: Exception) {
                e.printStackTrace()
                // 오류 처리
                Log.d(ContentValues.TAG, "캘린더 4-3 : ${e.message}")

            }
        }
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