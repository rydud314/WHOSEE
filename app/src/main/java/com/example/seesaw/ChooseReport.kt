package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ChooseReport : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_report)

        // 연간 리포트 보기 버튼 설정
        val btn_annual: Button = findViewById(R.id.btn_open_annual_report)
        btn_annual.setOnClickListener {
            val intent = Intent(this, Report_Annual::class.java)
            startActivity(intent)
        }

        // 리포트 보기 버튼 설정
        val btn_monthly: Button = findViewById(R.id.btn_open_monthly_report)
        btn_monthly.setOnClickListener {
            val intent = Intent(this, Report_Monthly::class.java)
            startActivity(intent)
        }
    }


}