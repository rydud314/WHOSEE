package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityLoginBinding
import com.example.seesaw.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth
    private var isIdChecked = false
    private var curEmail = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_signup)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()



        binding.btnIdCheck.setOnClickListener {
            var id = binding.idJoin.text.toString()
            val emailDomain = binding.spinnerEmailDomains.selectedItem.toString()

            var email = "$id$emailDomain"

            if (id.isEmpty()) {
                Toast.makeText(this, "사용할 아이디를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                idCheck(email)
            }
        }

        binding.btnJoin.setOnClickListener {

            var id = binding.idJoin.text.toString()
            val emailDomain = binding.spinnerEmailDomains.selectedItem.toString()
            var pw = binding.pwJoin.text.toString()
            var name = binding.nameJoin.text.toString()
            var tel1 = binding.telJoin01.text.toString()
            var tel2 = binding.telJoin02.text.toString()

            var email = "$id$emailDomain"

            if (id.isEmpty() || pw.isEmpty() || name.isEmpty() || tel1.isEmpty() || tel2.isEmpty()) {
                Toast.makeText(this, "공백란을 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (!isIdChecked || !email.equals(curEmail)) {
                Toast.makeText(this, "아이디 중복 확인을 완료해주세요", Toast.LENGTH_SHORT).show()
            } else {
                //회원가입
                val userInfo = hashMapOf(
                    "email" to email,
                    "pw" to pw,
                    "name" to name,
                    "tel" to "010-$tel1-$tel2"
                )

                auth.createUserWithEmailAndPassword(email, pw)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser

                            firestore.collection("id_list").document(user!!.uid)
                                .set(userInfo)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "회원정보 저장 완료!", Toast.LENGTH_SHORT).show()

                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Firestore에 정보 저장 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                                }

                            Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "회원가입 실패: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

//                firestore.collection("id_list")
//                    .document(email)
//                    .set(userInfo)
//                    .addOnSuccessListener {
//                        Toast.makeText(this, "회원가입 완료!", Toast.LENGTH_SHORT).show()
//                        val intent = Intent(this, Login::class.java)
//                        startActivity(intent)
//                        finish()
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(this, "회원가입 실패: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
            }
        }

    }

    private fun idCheck(email: String) {
        curEmail = email
        checkIdInDatabase(curEmail) { isAvailable ->
            if (isAvailable) {
                isIdChecked = true
                Toast.makeText(this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
            } else {
                isIdChecked = false
                Toast.makeText(this, "이미 존재하는 아이디입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIdInDatabase(email: String, callback: (Boolean) -> Unit) {
        firestore.collection("id_list")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    callback(true) // 사용 가능한 아이디
                } else {
                    callback(false) // 이미 존재하는 아이디
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "아이디 확인 중 오류 발생: ${exception.message}", Toast.LENGTH_SHORT).show()
                callback(false) // 오류가 발생하면 이미 존재하는 것으로 처리
            }
    }
}