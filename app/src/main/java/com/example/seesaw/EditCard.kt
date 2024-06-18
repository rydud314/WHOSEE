package com.example.seesaw

import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.seesaw.databinding.ActivityEditCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class EditCard : AppCompatActivity() {

    private lateinit var binding: ActivityEditCardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private var gender: String? = null
    private var imageUri: Uri? = null
    private var cardId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

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
            Glide.with(this).load(it.imageName).into(binding.ivAddedImage)
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
            Glide.with(this).load(it.imageName).into(binding.ivAddedImage)
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

        // 이미지 추가 버튼 클릭 이벤트
        binding.btnAddImage.setOnClickListener {
            openFileChooser()
        }

        //수정하기 버튼 누를 때 이벤트 > 명함 편집 완료
        binding.btnEditCard.setOnClickListener {
            // 명함 수정 코드
            updateCard(card)
        }
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            binding.ivAddedImage.setImageURI(imageUri)
            binding.ivAddedImage.visibility = View.VISIBLE

            // cardId와 이미지 이름 생성
            cardId = getRandomString(10)
        }
    }

    private fun uploadImage(cardId: String, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        imageUri?.let { uri ->
            val fileRef = storageRef.child("uploads/$cardId.jpg")
            fileRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        onSuccess(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    onFailure()
                    Log.e(TAG, "Upload failed", e)
                }
        }
    }

    private fun updateCard(card: Card?) {
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

            // Firestore에 업데이트
            card?.let {
                if (imageUri != null) {
                    uploadImage(it.cardId, { imageUrl ->
                        val updatedCardData = cardData.toMutableMap()
                        updatedCardData["imageName"] = imageUrl

                        firestore.collection("all_card_list").document(it.cardId)
                            .update(updatedCardData as Map<String, Any>)
                            .addOnSuccessListener {
                                Toast.makeText(this, "명함 수정 완료", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "명함 수정 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }) {
                        Toast.makeText(this, "이미지 업로드 실패", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    firestore.collection("all_card_list").document(it.cardId)
                        .update(cardData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "명함 수정 완료", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "명함 수정 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    fun getRandomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length).map { charset.random() }.joinToString("")
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
