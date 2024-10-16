package com.example.seesaw

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.service.controls.ControlsProviderService
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.core.content.ContextCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val pref = getSharedPreferences("userID", 0)
        //shared에 있는 'userID'이란 데이터를 불러온다는 뜻. 0 대신 MODE_PRIVATE라고 입력하셔도 됩니다.

        val savedid = pref.getString("id", "").toString()
        //1번째는 데이터 키 값이고 2번째는 키 값에 데이터가 존재하지않을때 대체 값 입니다.

        if (savedid == "") {  //자동 로그인 정보가 없을 경우, 로그인으로
            Log.d(TAG, "자동로그인 X")
//            val intent = Intent(this, Login::class.java)
//            startActivity(intent)
            Handler().postDelayed({
                // 여기서 MainActivity는 다음 화면으로 넘어갈 액티비티를 의미합니다. 실제 앱에서는 적절한 액티비티로 변경해야 합니다.
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
                finish() // SplashActivity 종료
            }, 2000) // 3000ms = 3초 딜레이
        } else {  //자동 로그인 정보가 있을 경우, 홈화면으로
            Log.d(TAG, "자동로그인 O")

            //큐알로 들어온 경우
            val uri = intent.data
            Log.d(ControlsProviderService.TAG, "uri = $uri")

            if (uri != null) {
                val cardId = uri.getQueryParameters("cardId").toString()
                Log.d(TAG, "Splash : cardid = $cardId")

                //fcm토큰 갱신
                updateFcmToken(savedid)

                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("uriExist", cardId)
                startActivity(intent)
                //Toast.makeText(this, "명함이 저장되었습니다.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                //fcm토큰 갱신
                updateFcmToken(savedid)

                //            val intent = Intent(this, MainActivity::class.java)
                Toast.makeText(this, "자동 로그인", Toast.LENGTH_SHORT).show()

                //api 33 이하를 제외한 기기에는 알림 권한 묻기
                askNotificationPermission()

//            startActivity(intent)
                Handler().postDelayed({
                    // 여기서 MainActivity는 다음 화면으로 넘어갈 액티비티를 의미합니다. 실제 앱에서는 적절한 액티비티로 변경해야 합니다.
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() // SplashActivity 종료
                }, 2000) // 3000ms = 3초 딜레이
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

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {    //알림 권한이 없을 경우
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showPermissionRationalDialog()
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun showPermissionRationalDialog() {
        AlertDialog.Builder(this)
            .setMessage("알림 권한이 없으면 알림을 받을 수 없습니다.")
            .setPositiveButton("권한 허용하기") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }.setNegativeButton("취소") { dialogInterface, _ ->
                dialogInterface.cancel()
            }.show()
    }

    private fun updateFcmToken(userId: String) {

        // Firestore에서 id_list 컬렉션에 접근하여 사용자 검색
        val firestore = Firebase.firestore
        firestore.collection("id_list")
            .whereEqualTo("email", userId)  // email 필드와 userId(즉, 저장된 이메일)가 일치하는 문서 검색
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // 검색된 문서에서 UID 가져오기
                    val document = documents.first()
                    val userUid = document.id  // 여기서 document.id는 Firestore 문서의 ID로, 각 사용자의 UID를 나타냄
                    Log.d(TAG, "uid = $userUid")

                    // UID를 사용하여 Realtime Database에서 FCM 토큰 업데이트
                    Firebase.messaging.token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val token = task.result
                            val user = mutableMapOf<String, Any>()
                            user["userUid"] = userUid
                            user["userEmail"] = userId
                            user["fcmToken"] = token


                            val DB_URL = "https://card-93c5d-default-rtdb.firebaseio.com/"
                            val DB_USERS = "users"

                            val databaseReference = Firebase.database(DB_URL).reference.child(DB_USERS).child(userUid)
                            databaseReference.updateChildren(user)
                                .addOnSuccessListener {
                                    Log.d(TAG, "FCM 토큰이 성공적으로 업데이트되었습니다.")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "FCM 토큰 업데이트 실패", e)
                                }
                        } else {
                            Log.w(TAG, "FCM 토큰을 가져오는 데 실패했습니다.", task.exception)
                        }
                    }
                } else {
                    Log.w(TAG, "해당 이메일로 등록된 사용자가 없습니다.")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "사용자 검색 중 오류 발생", e)
            }
    }
}