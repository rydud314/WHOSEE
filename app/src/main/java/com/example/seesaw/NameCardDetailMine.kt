package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.seesaw.databinding.ActivityNameCardDetailMineBinding
import com.example.seesaw.databinding.NameCardDetailBinding

class NameCardDetailMine : AppCompatActivity() {

    private lateinit var binding: ActivityNameCardDetailMineBinding
    private lateinit var detailBinding: NameCardDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameCardDetailMineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 포함된 레이아웃 바인딩 초기화
        detailBinding = NameCardDetailBinding.bind(binding.root.findViewById(R.id.my_name_card_detail_container))

        // 인텐트로부터 데이터 가져오기
        val card = intent.getParcelableExtra<Card>("card")

        // 뷰에 데이터 설정
        card?.let {
            binding.tvName.text = it.name
            //binding.tvJob.text = "Job : " + it.job
            binding.tvIntroduction.text = "Introduction : " + it.workplace
            //binding.tvWorkplace.text = "Workplace : " + it.workplace
            //binding.tvGender.text = "Gender : " + it.gender
            //binding.tvPosition.text = "Position : " + it.position
            binding.tvTel.text = "Tel : " + it.tel
            //binding.tvEmail.text = "Email : " + it.email
            binding.tvSns.text = "SNS : " + it.sns
            binding.tvPortfolio.text = "Portfolio : " + it.pofol

//            // 이미지 설정
//            loadCardImage(it.imageName)
        }

        // 뷰에 데이터 설정
        card?.let {
            detailBinding.tvName.text = it.name
            detailBinding.tvJob.text = it.job
            detailBinding.tvIntroduction.text = it.introduction
            detailBinding.tvWorkplace.text = it.workplace
            detailBinding.tvGender.text = it.gender
            detailBinding.tvPosition.text = it.position
            //detailBinding.tvTel.text = "Tel : " + it.tel
            detailBinding.tvEmail.text = it.email
//            detailBinding.tvSns.text = "SNS : " + it.sns
//            detailBinding.tvPortfolio.text = "Portfolio : " + it.pofol

            // 이미지 설정
            loadCardImage(it.imageName)
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

    private fun loadCardImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(detailBinding.cardImage) // 이미지뷰의 ID를 여기에 맞게 설정
    }
}
