package com.example.seesaw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.seesaw.databinding.ActivityNameCardDetailOthersBinding

class NameCardDetailOthers : AppCompatActivity() {

    private lateinit var binding: ActivityNameCardDetailOthersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameCardDetailOthersBinding.inflate(layoutInflater)
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

            // 이미지 설정
            loadCardImage(it.imageName)
        }
    }

    //        // 버튼 클릭 리스너 설정
//        binding.btnEditCard.setOnClickListener {
//            val intent = Intent(this, EditCard::class.java).apply {
//                putExtra("card", card)
//            }
//            startActivity(intent)
//        }


    private fun loadCardImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(binding.cardImage) // 이미지뷰의 ID를 여기에 맞게 설정
    }
}
