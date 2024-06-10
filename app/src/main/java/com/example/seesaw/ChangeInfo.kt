package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityChangeInfoBinding

class ChangeInfo : AppCompatActivity() {

    private lateinit var binding: ActivityChangeInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnChange.setOnClickListener{
            //변경하기 버튼 클릭이벤트 구현
        }
    }
}