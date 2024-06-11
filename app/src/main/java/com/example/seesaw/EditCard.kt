package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityEditCardBinding
import com.example.seesaw.databinding.ActivityMakeCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditCard : AppCompatActivity() {

    private lateinit var binding: ActivityEditCardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var gender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_edit_card)

        // Firebase 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 명함 정보를 firebase에서 가져와 edittext에 띄움
        // 가져오는 코드

        // 인텐트로부터 데이터 가져오기
        val card = intent.getParcelableExtra<Card>("card")

        // EditText 힌트에 데이터 설정
        card?.let {
            binding.etJob.hint = it.job
            binding.etName.hint = it.name
            binding.etWorkplace.hint = it.workplace
            binding.etIntroduction.hint = it.introduction
            binding.tvGender.hint = it.gender
            binding.etPosition.hint = it.position
            binding.etTel.hint = it.tel
            binding.etEmail.hint = it.email
            binding.etSns.hint = it.sns
            binding.etPortfolio.hint = it.pofol
        }

        // EditText에 선입력 되어있도록
        card?.let {
            binding.etJob.setText(it.job)
            binding.etName.setText(it.name)
            binding.etWorkplace.setText(it.workplace)
            binding.etIntroduction.setText(it.introduction)
            binding.tvGender.setText(it.gender)
            binding.etPosition.setText(it.position)
            binding.etTel.setText(it.tel)
            binding.etEmail.setText(it.email)
            binding.etSns.setText(it.sns)
            binding.etPortfolio.setText(it.pofol)
        }

        // 성별 선택에 따른 이벤트 코드
        binding.btnMale.setOnClickListener {
            // 성별 설정
            gender = "남"
            binding.tvGender.setText(gender)
            binding.btnMale.setBackgroundResource(R.drawable.selected_box_shape)
            binding.btnFemale.setBackgroundResource(R.drawable.box_shape)
        }

        binding.btnFemale.setOnClickListener {
            // 성별 설정
            gender = "여"
            binding.tvGender.setText(gender)
            binding.btnFemale.setBackgroundResource(R.drawable.selected_box_shape)
            binding.btnMale.setBackgroundResource(R.drawable.box_shape)
        }

        //수정하기 버튼 누를 때 이벤트 > 명함 편집 완료

        // 명함 수정 버튼 설정
//        val btn_edit_card: Button = findViewById(R.id.btn_edit_card)
        binding.btnEditCard.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}