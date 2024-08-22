package com.example.seesaw

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.seesaw.databinding.ActivityNameCardDetailOthersBinding
import com.example.seesaw.databinding.NameCardDetailBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class NameCardDetailOthers : AppCompatActivity() {

    private lateinit var binding: ActivityNameCardDetailOthersBinding
    private lateinit var detailBinding: NameCardDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNameCardDetailOthersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 포함된 레이아웃 바인딩 초기화
        detailBinding = NameCardDetailBinding.bind(binding.root.findViewById(R.id.others_name_card_detail_container))

        // 인텐트로부터 데이터 가져오기
        val card = intent.getParcelableExtra<Card>("card")

        // 뷰에 데이터 설정
        card?.let {
            binding.tvName.text = it.name
            //binding.tvJob.text = "Job: " + it.job
            //binding.tvIntroduction.text = "Introduction: " + it.introduction
            //binding.tvWorkplace.text = "Workplace: " + it.workplace
            //binding.tvGender.text = "Gender: " + it.gender
            //binding.tvPosition.text = "Position: " + it.position
            binding.tvTel.text = "Tel: " + it.tel
            //binding.tvEmail.text = "Email: " + it.email
            binding.tvSns.text = "SNS: " + it.sns
            binding.tvPortfolio.text = "Portfolio: " + it.pofol

            // 이미지 설정
            loadCardImage(it.imageName)
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

        // 1:1 채팅하기 버튼 클릭 이벤트
        binding.btnChat.setOnClickListener {
            val currentUserId = Firebase.auth.currentUser?.uid.toString()
            val cardId = card?.cardId
            Log.d(ContentValues.TAG, "cardId = $cardId")

            val firestore = FirebaseFirestore.getInstance()

            if (cardId != null) {
                // 모든 문서를 조회한 후 해당 cardId를 포함하는지 확인
                firestore.collection("my_card_list")
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        var otherUserId: String? = null

                        for (document in querySnapshot.documents) {
                            val cardsList = document.get("cards") as? List<Map<String, String>>
                            val containsCardId = cardsList?.any { it["cardId"] == cardId } == true

                            if (containsCardId) {
                                otherUserId = document.id
                                break
                            }
                        }

                        if (otherUserId != null) {
                            Log.d(ContentValues.TAG, "otherUserId = $otherUserId")

                            // 상대방의 wallet_list에서 모든 카드 ID를 가져옴
                            firestore.collection("wallet_list")
                                .document(otherUserId)
                                .get()
                                .addOnSuccessListener { document ->
                                    val otherUserCardsList = document.get("cards") as? List<Map<String, String>>

                                    // 현재 사용자의 my_card_list에서 카드 ID 목록을 가져옴
                                    firestore.collection("my_card_list")
                                        .document(currentUserId)
                                        .get()
                                        .addOnSuccessListener { myCardListDoc ->
                                            val myCardsList = myCardListDoc.get("cards") as? List<Map<String, String>>

                                            // 겹치는 카드 ID가 있는지 확인
                                            val hasUserCard = myCardsList?.any { myCard ->
                                                otherUserCardsList?.any { otherCard ->
                                                    otherCard["cardId"] == myCard["cardId"]
                                                } == true
                                            } == true

                                            Log.d(ContentValues.TAG, "currentUserId = $currentUserId")

                                            if (hasUserCard) {
                                                // 두 사용자가 서로의 명함을 가지고 있으면 채팅 화면으로 이동
                                                Log.d(TAG, "$currentUserId 와 $otherUserId 가 채팅을 시작합니다")

//                                                val intent = Intent(this, MessageActivity::class.java)
//                                                intent.putExtra("destinationUid", otherUserId)
//                                                startActivity(intent)

                                                val sharedPreferences = getSharedPreferences("other", 0)
                                                val editor = sharedPreferences.edit()
                                                val intent = Intent(this, MessageActivity::class.java)
                                                intent.putExtra("destinationUid", otherUserId)
                                                editor.putString("userState", "Login")
                                                editor.apply()
                                                startActivity(intent)
                                            } else {
                                                // 상대방이 사용자의 명함을 가지고 있지 않은 경우
                                                Toast.makeText(this, "상대방이 당신의 명함을 가지고 있지 않습니다.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this, "사용자의 명함 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "상대방의 명함 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // 상대방을 찾지 못한 경우
                            Toast.makeText(this, "상대 사용자 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "명함 조회에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "카드 ID를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
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
            .into(detailBinding.cardImage) // 이미지뷰의 ID를 여기에 맞게 설정
    }
}
