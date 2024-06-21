package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityNameCardDetailOthersBinding
import com.example.seesaw.databinding.ActivityShareCardDetailBinding

class ShareCardDetail : AppCompatActivity() {

    private lateinit var binding: ActivityShareCardDetailBinding
    private lateinit var frag3Share: Frag3_Share


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 인텐트로부터 데이터 가져오기
        val card = intent.getParcelableExtra<Card>("card")

        // 뷰에 데이터 설정
        card?.let {
            binding.tvName.text = "Name: " + it.name
            binding.tvJob.text = "Job: " + it.job
            binding.tvIntroduction.text = "Introduction: " + it.introduction
            binding.tvWorkplace.text = "Workplace: " + it.workplace
            binding.tvGender.text = "Gender: " + it.gender
            binding.tvPosition.text = "Position: " + it.position
            binding.tvTel.text = "Tel: " + it.tel
            binding.tvEmail.text = "Email: " + it.email
            binding.tvSns.text = "SNS: " + it.sns
            binding.tvPortfolio.text = "Portfolio: " + it.pofol
        }

//        // 버튼 클릭 리스너 설정
        binding.btnSaveCard.setOnClickListener {
            val intent = Intent(this, Card::class.java).apply {
                putExtra("card", card)

                frag3Share = Frag3_Share.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag3Share).commit()
           }
           startActivity(intent)
        }
    }
}
