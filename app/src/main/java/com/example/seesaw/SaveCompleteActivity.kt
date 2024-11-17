package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivitySaveCompleteBinding

class SaveCompleteActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaveCompleteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveCompleteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 완료 버튼 클릭
        binding.btnComplete.setOnClickListener {
            // 메인 화면으로 돌아가기
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP // 스택 초기화
            startActivity(intent)
            finish()
        }

        // 추가 등록 버튼 클릭
        binding.btnAddMore.setOnClickListener {
            // 카메라 스캔 화면으로 돌아가기
            val intent = Intent(this, CameraActivity::class.java)
            finish()
        }
    }
}
