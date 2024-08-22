package com.example.seesaw

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.seesaw.databinding.ActivityShareCardDetailBinding
import com.example.seesaw.databinding.NameCardDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

private const val PACKAGE_NAME = "com.example.seesaw"
class ShareCardDetail : AppCompatActivity() {

    private lateinit var binding: ActivityShareCardDetailBinding
    private lateinit var detailBinding: NameCardDetailBinding

    private lateinit var frag3Share: Frag3_Share2
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        //uid가 유효하지 않을 때
        if (uid == null){
            Log.d(TAG, "shareDetail : uid없음")
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()

        }
        //uid가 유효할 때
        else {
            // 포함된 레이아웃 바인딩 초기화
            detailBinding =
                NameCardDetailBinding.bind(binding.root.findViewById(R.id.shared_name_card_detail_container))

            // 인텐트로부터 데이터 가져오기
            var cardId = intent.getSerializableExtra("uriExist").toString()
            Log.d(TAG, "share111 : $cardId")

            cardId = cardId.replace("[", "")
            cardId = cardId.replace("]", "")
            Log.d(TAG, "share : $cardId")

            // 자신의 카드 정보 가져오기
            firestore.collection("all_card_list").whereEqualTo("cardId", cardId).get()
                .addOnSuccessListener { querySnapshot ->
                    val documents = querySnapshot.documents.toMutableList()
                    Log.d(ContentValues.TAG, "querysnapshot complete")

                    if (documents != null && documents.isNotEmpty()) {
                        Log.d(ContentValues.TAG, "result.document is not null")

                        for (document in documents) {
                            val cardId = document["cardId"].toString()
                            val name = document["name"].toString()
                            val workplace = document["workplace"].toString()
                            val introduction = document["introduction"].toString()
                            val email = document["email"].toString()
                            val position = document["position"].toString()
                            val gender = document["gender"].toString()
                            val imageName = document["imageName"].toString()
                            val job = document["job"].toString()
                            val pofol = document["pofol"].toString()
                            val sns = document["sns"].toString()
                            val tel = document["tel"].toString()

                            Log.d(ContentValues.TAG, "cardData => $name($cardId)")

                            // 뷰에 데이터 설정
                            binding.tvName.text = name
                            //binding.tvJob.text = "Job: " + it.job
                            //binding.tvIntroduction.text = "Introduction: " + introduction
                            //binding.tvWorkplace.text = "Workplace: " + it.workplace
                            //binding.tvGender.text = "Gender: " + it.gender
                            //binding.tvPosition.text = "Position: " + it.position
                            binding.tvTel.text = tel
                            //binding.tvEmail.text = "Email: " + it.email
                            binding.tvSns.text = "SNS: " + sns
                            binding.tvPortfolio.text = "Portfolio: " + pofol

                            // 이미지 설정
                            loadCardImage(imageName)

                            // 뷰에 데이터 설정
                            detailBinding.tvName.text = name
                            detailBinding.tvJob.text = job
                            detailBinding.tvIntroduction.text = introduction
                            detailBinding.tvWorkplace.text = workplace
                            detailBinding.tvGender.text = gender
                            detailBinding.tvPosition.text = position
                            //detailBinding.tvTel.text = "Tel : " + it.tel
                            detailBinding.tvEmail.text = email
//            detailBinding.tvSns.text = "SNS : " + it.sns
//            detailBinding.tvPortfolio.text = "Portfolio : " + it.pofol

                            // 이미지 설정
                            loadCardImage(imageName)
                        }
                    }

                    // 버튼 클릭 리스너 설정
                    binding.btnSaveCard.setOnClickListener {
                        if (uid != null) {
                            saveToMyCardList(uid, cardId)
                        }
                    }
                }
        }
    }

    private fun loadCardImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(detailBinding.cardImage)
    }

    private fun saveToMyCardList(userId: String, cardId: String) {
        val checkRef = firestore.collection("my_card_list").document(userId)
        checkRef.get().addOnSuccessListener { doc ->
            
            if (doc.exists()) {
                // 문서가 존재하면
                val existingArray = doc.get("cards") as? ArrayList<HashMap<String, Any>>
                if (existingArray != null) {
                    // 배열이 이미 존재하면

                    // 내 명함인지 확인
                    var isExisted = false
                    for (i in existingArray) {
                        if (i.containsValue(cardId)) {
                            isExisted = true
                            break
                        }
                    }
                    if (!isExisted) {
                        //명함 공유 받기 가능 -> 내 명함이 아니라는 뜻
                        //wallet_list에 저장하는 코드
                        val myCardRef = firestore.collection("wallet_list").document(userId)
                        myCardRef.get().addOnSuccessListener { document ->
                            val cardData = hashMapOf<String, Any>(
                                "cardId" to cardId
                            )
                            if (document.exists()) {
                                // 문서가 존재하면
                                val existingArray =
                                    document.get("cards") as? ArrayList<HashMap<String, Any>>
                                if (existingArray != null) {
                                    // 배열이 이미 존재하면 새 데이터 추가

                                    // 이미 가지고 있는 명함인지 확인
                                    var isExisted = false
                                    for (i in existingArray) {
                                        if (i.containsValue(cardId)) {
                                            isExisted = true
                                            break
                                        }
                                    }
                                    if (!isExisted) {
                                        existingArray.add(cardData)
                                        myCardRef.update("cards", existingArray)
                                    } else {
                                        // 이미 가지고 있는 명함일 때
                                        Toast.makeText(this, "이미 저장한 명함입니다.", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // 배열이 존재하지 않으면 새로운 배열 생성 후 데이터 추가
                                    myCardRef.set(mapOf("cards" to arrayListOf(cardData)))
                                }
                            } else {
                                // 문서가 없으면 새 문서 생성 후 데이터 추가
                                myCardRef.set(mapOf("cards" to arrayListOf(cardData)))
                            }
                            // 성공 메시지 표시 및 액티비티 종료
                            Log.d(TAG, "명함이 저장되었습니다.")
                            Toast.makeText(this, "명함을 저장하였습니다.", Toast.LENGTH_SHORT).show()
                            finishCreation()
                        }.addOnFailureListener { e ->
                            Log.d(TAG, "명함 저장 실패")
                        }

                    } else {
                        // 공유 받은 명함이 내 명함일 때
                        Toast.makeText(this, "나의 명함은 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 배열이 존재하지 않으면
                    Log.d(TAG, "내 명함 체크 실패")
                }
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "내 명함 체크 실패(failureListener)")
        }

    }

    private fun finishCreation() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
