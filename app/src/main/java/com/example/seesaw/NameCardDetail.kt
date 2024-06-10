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
        val card = intent.getParcelableExtra<Card>("card")

        // 뷰에 데이터 설정
        card?.let {
            binding.tvName.text = "Name : " + it.name
            binding.tvJob.text = "Job : " + it.job
            binding.tvIntroduction.text = "Introduction : " + it.workplace
            binding.tvWorkplace.text = "Workplace : " + it.workplace
            binding.tvAgeGender.text = "Age/Gender : " + it.age + "/" + it.gender
            binding.tvAnnual.text = "Annual : " + it.annual
            binding.tvTel.text = "Tel : " + it.tel
            binding.tvEmail.text = "Email : " + it.email
            binding.tvSns.text = "SNS : " + it.sns
            binding.tvPortfolio.text = "Portfolio : " + it.pofol
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
