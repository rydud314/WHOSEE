package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PrintDetectedText : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print_detected_text)

        val resultTextView: TextView = findViewById(R.id.resultTextView)

        // Intent로 전달된 결과 텍스트 가져오기
        val resultText = intent.getStringExtra("RESULT_TEXT")

        // 결과 텍스트 표시
        resultTextView.text = resultText ?: "No Data Found"
    }
}