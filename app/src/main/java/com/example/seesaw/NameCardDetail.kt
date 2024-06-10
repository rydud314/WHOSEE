package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityNameCardDetailBinding

class NameCardDetail : AppCompatActivity() {

    private lateinit var binding: ActivityNameCardDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트로부터 데이터 가져오기
        val card = intent.getParcelableExtra<NameCardData>("card")

        // 뷰에 데이터 설정
        card?.let {
            binding.tvName.text = "name : " + it.name
            binding.tvJob.text = "job : " + it.job
            binding.tvAddress.text = "address : " + it.address
            binding.tvAgeGender.text = "age/gender : " + it.age + "/" + it.Gender
            binding.tvCurJob.text = "annual : " + it.annual
            binding.tvEmail.text = "email : " + it.email
        }

        // 버튼 클릭 리스너 설정
        binding.btnEditCard.setOnClickListener {
            //val intent = Intent(this, EditCard::class.java)
            val intent = Intent(this, EditCard::class.java).apply {
                putExtra("card", card)
            }
            startActivity(intent)
        }
    }
}
