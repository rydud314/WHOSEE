package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
            //이미지도 넣어야 함
            val name = binding.etName.text.toString().trim()
            val job = binding.etJob.text.toString().trim()
            val introduction = binding.etIntroduction.text.toString().trim()
            val workplace = binding.etWorkplace.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val gender = binding.tvGender.text.toString().trim()
            val position = binding.etPosition.text.toString().trim()
            val tel = binding.etTel.text.toString().trim()
            val sns = binding.etSns.text.toString().trim()
            val pofol = binding.etPortfolio.text.toString().trim()

            if (name.isEmpty()) {
                // 이름이 비어있을 경우 경고 메시지 표시
                Toast.makeText(this, "이름은 필수 입력사항입니다.", Toast.LENGTH_SHORT).show()
            } else {
                // 업데이트할 데이터를 Map으로 준비
                val cardData = mapOf(
                    "name" to name,
                    "job" to job,
                    "introduction" to introduction,
                    "workplace" to workplace,
                    "email" to email,
                    "gender" to gender,
                    "position" to position,
                    "tel" to tel,
                    "sns" to sns,
                    "pofol" to pofol
                )

//                var check = false;

                // Firestore에 업데이트
                card?.let {
                    firestore.collection("all_card_list").document(it.cardId)
                        .update(cardData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "명함 수정 완료", Toast.LENGTH_SHORT).show()
//                            check = true;
//                            MainActivity로 이동
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "명함 수정 실패: ${e.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                }

//                if (check == true) {
//                    val intent = Intent(this, ChooseEditCard::class.java)
//                    startActivity(intent)
//                }

            }
        }
    }
}