package com.example.seesaw

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
//        setContentView(R.layout.activity_login)

        binding.btnLogin.setOnClickListener {
            val id = binding.loginId.text.toString()
            val password = binding.loginPw.text.toString()

            if (id.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {

                auth.signInWithEmailAndPassword(id, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            if (binding.chkAutoLogin.isChecked) {
                                saveDate(id) // 자동로그인
                            }
                            Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this,
                                "로그인 실패: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

//                firestore.collection("id_list")
//                    .document(id)
//                    .get()
//                    .addOnSuccessListener { document ->
//                        if (document.exists()) {
//                            val savedPassword = document.getString("pw")
//                            if (savedPassword.equals(password)) {
//                                if (binding.chkAutoLogin.isChecked) {
//                                    saveDate(id) // 자동로그인
//                                }
//                                Toast.makeText(this, "로그인 성공", Toast.LENGTH_SHORT).show()
//                                // 로그인 성공 시 다음 액티비티로 이동
//                                val intent = Intent(this, MainActivity::class.java)
//                                startActivity(intent)
////                                finish() // 현재 액티비티 종료
//                            } else {
//                                Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
//                            }
//                        } else {
//                            Toast.makeText(this, "해당 아이디가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//                    .addOnFailureListener { e ->
//                        Toast.makeText(this, "오류가 발생했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
//                    }
            }

        }

//        val btn_login : Button = findViewById(R.id.btn_login)
//        btn_login.setOnClickListener{
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }

        val btn_signup: Button = findViewById(R.id.btn_signup)
        btn_signup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }

    private fun saveDate(id: String) {
        val pref = getSharedPreferences("userID", MODE_PRIVATE) //shared key 설정
        val edit = pref.edit() // 수정모드
        edit.putString("id", id) // 값 넣기
        edit.apply() // 적용하기
    }
}