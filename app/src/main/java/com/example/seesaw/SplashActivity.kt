package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.service.controls.ControlsProviderService
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Firestore 인스턴스를 초기화합니다.
        val database: FirebaseFirestore = Firebase.firestore

        val pref = getSharedPreferences("userID", 0)
        // shared에 있는 'userID'이란 데이터를 불러온다는 뜻. 0 대신 MODE_PRIVATE라고 입력하셔도 됩니다.

        val savedid = pref.getString("id", "").toString()
        // 1번째는 데이터 키 값이고 2번째는 키 값에 데이터가 존재하지 않을 때 대체 값입니다.

        if (savedid == "") {  // 자동 로그인 정보가 없을 경우, 로그인으로
            Log.d(TAG, "자동로그인 X")
//            val intent = Intent(this, Login::class.java)
//            startActivity(intent)
            Handler().postDelayed({
                // 여기서 MainActivity는 다음 화면으로 넘어갈 액티비티를 의미합니다. 실제 앱에서는 적절한 액티비티로 변경해야 합니다.
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
                finish() // SplashActivity 종료
            }, 2000) // 3000ms = 3초 딜레이
        } else {  // 자동 로그인 정보가 있을 경우, 홈 화면으로
            Log.d(TAG, "자동로그인 O")

            // QR로 들어온 경우
            val uri = intent.data
            Log.d(ControlsProviderService.TAG, "uri = $uri")

            if (uri != null) {
                val cardId = uri.getQueryParameters("cardId").toString()
                Log.d(TAG, "Splash : cardid = $cardId")

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("uriExist", cardId)
                startActivity(intent)
                //Toast.makeText(this, "명함이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                //            val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "자동 로그인", Toast.LENGTH_SHORT).show()
//            startActivity(intent)
                Handler().postDelayed({
                    // 여기서 MainActivity는 다음 화면으로 넘어갈 액티비티를 의미합니다. 실제 앱에서는 적절한 액티비티로 변경해야 합니다.
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // SplashActivity 종료
                }, 2000) // 3000ms = 3초 딜레이
            }

            // 사용자의 UID를 Firebase Auth에서 가져오고 FCM 토큰을 Firestore에 저장합니다.
            val currentUser = FirebaseAuth.getInstance().currentUser
            currentUser?.let { user ->
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val token = task.result
                        Log.d(TAG, "FCM 토큰 갱신: $token")

                        database.collection("id_list").document(user.uid)
                            .update("fcmToken", token)
                            .addOnSuccessListener {
                                Log.d(TAG, "FCM 토큰이 Firestore에 성공적으로 저장되었습니다.")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Firestore에 FCM 토큰 저장 실패", e)
                            }
                    } else {
                        Log.w(TAG, "FCM 토큰 가져오기 실패", task.exception)
                    }
                }
            }
        }

//        // Handler를 사용하여 3초 후에 MainActivity로 이동
//        Handler().postDelayed({
//            // 여기서 MainActivity는 다음 화면으로 넘어갈 액티비티를 의미합니다. 실제 앱에서는 적절한 액티비티로 변경해야 합니다.
//            val intent = Intent(this, Login::class.java)
//            startActivity(intent)
//            finish() // SplashActivity 종료
//        }, 2000) // 3000ms = 3초 딜레이
    }
}
