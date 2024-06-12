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
import com.example.seesaw.databinding.ActivityMakeCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MakeCard : AppCompatActivity() {
    private lateinit var binding: ActivityMakeCardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private var gender: String? = null
    private var imageUri: Uri? = null
    private var cardId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

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

        binding.btnAddImage.setOnClickListener {
            openFileChooser()
        }

        binding.btnCreateCard.setOnClickListener {
            createCard()
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

    private fun uploadImage() {
        imageUri?.let { uri ->
            val fileRef = storageRef.child("uploads/$cardId.jpg")
            fileRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        Toast.makeText(this, "Upload successful. URL: $uri", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Upload failed", e)
                }
        }
    }

    private fun createCard() {
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

        if (cardId == "") {
            cardId = getRandomString(10)
        }

        Log.d(TAG, "cardId =  $cardId")

        if (name.isEmpty()) {
            // 이름이 비어있을 경우 경고 메시지 표시
            Toast.makeText(this, "이름은 필수 입력사항입니다.", Toast.LENGTH_SHORT).show()
        } else {
            val currentUser = auth.currentUser
            val uid = currentUser?.uid


            //val cardRef = firestore.collection("all_card_list").document(cardId)

            uid?.let { userId ->
                val cardRef = firestore.collection("all_card_list").document(cardId)

                cardRef.get()
                    .addOnSuccessListener { document ->
                        val imageName = if (imageUri != null) "uploads/$cardId.jpg" else "uploads/default.jpg"
                        val cardData = hashMapOf<String, Any>(
                            "cardId" to cardId!!,
                            "name" to name,
                            "job" to job,
                            "introduction" to introduction,
                            "workplace" to workplace,
                            "email" to email,
                            "gender" to gender,
                            "position" to position,
                            "tel" to tel,
                            "sns" to sns,
                            "pofol" to pofol,
                            "imageName" to imageName
                        )

                        if (document.exists()) {
                            Log.d(TAG,"명함 아이디 중복 체크 실패")
                        } else {
                            // 문서가 없으면 새 문서 생성 후 데이터 추가
                            cardRef.set(cardData)
                        }
                        // 성공 메시지 표시 및 액티비티 종료
                        Log.d(TAG, "명함이 생성되었습니다.")


                        // my_card_list에 저장하는 코드
                        val myCardRef = firestore.collection("my_card_list").document(userId)
                        //                          Toast.makeText(this, "$userId", Toast.LENGTH_SHORT).show()
                        myCardRef.get()
                            .addOnSuccessListener { document ->
                                val cardData1 = hashMapOf<String, Any>(
                                    "cardId" to cardId!!
                                )
                                if (document.exists()) {
                                    // 문서가 존재하면
                                    val existingArray = document.get("cards") as? ArrayList<HashMap<String, Any>>?
                                    if (existingArray != null) {
                                        // 배열이 이미 존재하면 새 데이터 추가
                                        existingArray.add(cardData1)
                                        myCardRef.update("cards", existingArray)
                                    } else {
                                        // 배열이 존재하지 않으면 새로운 배열 생성 후 데이터 추가
                                        myCardRef.set(mapOf("cards" to arrayListOf(cardData1)))
                                    }
                                } else {
                                    // 문서가 없으면 새 문서 생성 후 데이터 추가
                                    myCardRef.set(mapOf("cards" to arrayListOf(cardData1)))
                                }
                                // 성공 메시지 표시 및 액티비티 종료
                                Log.d(TAG, "명함이 저장되었습니다.")

                                if (imageUri != null) {
                                    uploadImage()
                                }

                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                // 실패 메시지 표시
                                Log.d(TAG, "명함 저장 실패")
                            }
                    }
                    .addOnFailureListener { e ->
                        // 실패 메시지 표시
                        Log.d(TAG, "명함 생성 실패")
                    }
            }
        }
    }


    fun getRandomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}
