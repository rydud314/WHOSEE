package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Handler를 사용하여 3초 후에 MainActivity로 이동
        Handler().postDelayed({
            // 여기서 MainActivity는 다음 화면으로 넘어갈 액티비티를 의미합니다. 실제 앱에서는 적절한 액티비티로 변경해야 합니다.
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish() // SplashActivity 종료
        }, 2000) // 3000ms = 3초 딜레이
    }
}