package com.example.seesaw

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.seesaw.databinding.ActivityMakeCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MakeCard : AppCompatActivity() {
    private lateinit var binding: ActivityMakeCardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var gender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

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

        binding.btnCreateCard.setOnClickListener {

            var name = binding.etName.text.toString()
            var job = binding.etJob.text.toString()
            var introduction = binding.etIntroduction.text.toString()
            var workplace = binding.etWorkplace.text.toString()
            var age = binding.etAge.text.toString()
            var gender = binding.tvGender.text.toString()
            var annual = binding.etAnnual.text.toString()
            var tel = binding.etTel.text.toString()
            var sns = binding.etSns.text.toString()
            var pofol = binding.etPortfolio.text.toString()
            val cardId = getRandomString(10)
            Log.d(TAG, "cardId =  $cardId")

            if (name.isEmpty()) {
                // 이름이 비어있을 경우 경고 메시지 표시
                Toast.makeText(this, "이름은 필수 입력사항입니다.", Toast.LENGTH_SHORT).show()
            } else {
                val currentUser = auth.currentUser
                val uid = currentUser?.uid

                uid?.let { userId ->
                    val cardRef = firestore.collection("all_card_list").document("card_list")
                    Toast.makeText(this, "UID = $userId", Toast.LENGTH_SHORT).show()

                    cardRef.get()
                        .addOnSuccessListener { document ->
                            val cardData = hashMapOf(
                                "cardId" to cardId,
                                "name" to name,
                                "job" to job,
                                "introduction" to introduction,
                                "workplace" to workplace,
                                "age" to age,
                                "gender" to gender,
                                "annual" to annual,
                                "tel" to tel,
                                "sns" to sns,
                                "pofol" to pofol
                            )

                            if (document.exists()) {
                                // 문서가 존재하면
                                val existingArray = document.get("cards") as? ArrayList<HashMap<String, String>>?
                                if (existingArray != null) {
                                    // 배열이 이미 존재하면 새 데이터 추가
                                    existingArray.add(cardData)
                                    cardRef.update("cards", existingArray)
                                } else {
                                    // 배열이 존재하지 않으면 새로운 배열 생성 후 데이터 추가
                                    cardRef.set(mapOf("cards" to arrayListOf(cardData)))
                                }
                            } else {
                                // 문서가 없으면 새 문서 생성 후 데이터 추가
                                cardRef.set(mapOf("cards" to arrayListOf(cardData)))
                            }

                            // 성공 메시지 표시 및 액티비티 종료
                            Log.d(TAG, "명함이 생성되었습니다.")


                            //my_card_list에 저장하는 코드
                            val cardRef = firestore.collection("my_card_list").document(userId)
//                          Toast.makeText(this, "$userId", Toast.LENGTH_SHORT).show()
                            cardRef.get()
                                .addOnSuccessListener { document ->
                                    val cardData1 = hashMapOf(
                                        "cardId" to cardId
                                    )

                                    if (document.exists()) {
                                        // 문서가 존재하면
                                        val existingArray = document.get("cards") as? ArrayList<HashMap<String, String>>?
                                        if (existingArray != null) {
                                            // 배열이 이미 존재하면 새 데이터 추가
                                            existingArray.add(cardData1)
                                            cardRef.update("cards", existingArray)
                                        } else {
                                            // 배열이 존재하지 않으면 새로운 배열 생성 후 데이터 추가
                                            cardRef.set(mapOf("cards" to arrayListOf(cardData1)))
                                        }
                                    } else {
                                        // 문서가 없으면 새 문서 생성 후 데이터 추가
                                        cardRef.set(mapOf("cards" to arrayListOf(cardData1)))
                                    }

                                    // 성공 메시지 표시 및 액티비티 종료
                                    Log.d(TAG, "명함이 저장되었습니다.")
                                    
                                }
                                .addOnFailureListener { e ->
                                    // 실패 메시지 표시
                                    Log.d(TAG, "명함 저장 실패")
                                }

                            finish()
                        }
                        .addOnFailureListener { e ->
                            // 실패 메시지 표시
                            Log.d(TAG, "명함 생성 실패")
                            
                        }
                }
            }
        }
    }

    fun getRandomString(length: Int) : String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }
}