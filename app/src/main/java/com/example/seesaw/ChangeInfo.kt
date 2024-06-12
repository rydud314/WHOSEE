package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityChangeInfoBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChangeInfo : AppCompatActivity() {

    private lateinit var binding: ActivityChangeInfoBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        binding.tvPwHint.visibility = View.GONE
        binding.tvPwError.visibility = View.GONE

        //새 비밀번호
        binding.etPwNew.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 전
                binding.tvPwHint.visibility = View.GONE
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중
                val pwNew = s.toString().trim()

                if (pwNew.length < 6 || pwNew.length > 10) {
                    binding.tvPwHint.visibility = View.VISIBLE
                } else {
                    binding.tvPwHint.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 후
            }
        })

        //비밀번호 확인
        binding.etPwNewAgain.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // 텍스트 변경 전
                binding.tvPwError.visibility = View.GONE

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 텍스트 변경 중
                val pwNew = binding.etPwNew.text.toString().trim()
                val pwNewAgain = s.toString().trim()

                if (pwNew == pwNewAgain) {
                    binding.tvPwError.visibility = View.GONE
                } else {
                    binding.tvPwError.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // 텍스트 변경 후
            }
        })

        binding.btnChange.setOnClickListener{
            val pwNew = binding.etPwNew.text.toString().trim()
            val pwNewAgain = binding.etPwNewAgain.text.toString().trim()

//            if (pwNew.length < 6 || pwNew.length > 10) {
//                binding.tvPwHint.visibility = View.VISIBLE
//            } else {
//                binding.tvPwHint.visibility = View.GONE
//            }
//
//            if (pwNew == pwNewAgain) {
//                binding.tvPwError.visibility = View.GONE
//            } else {
//                binding.tvPwError.visibility = View.VISIBLE
//            }

            //공백 제거 + 빈문자열 인지 한번에 가능 -> isnotblank
            if (pwNew.isNotBlank() && pwNewAgain.isNotBlank()) {
                if (pwNew == pwNewAgain) {
                    val user = auth.currentUser
                    if (user != null) {
                        val uid = user.uid
                        val userRef = firestore.collection("id_list").document(uid)
                        userRef.update("pw", pwNew)
                            .addOnSuccessListener {
                                Toast.makeText(this, "비밀번호가 업데이트 되었습니다.", Toast.LENGTH_SHORT).show()
                                // 홈화면으로 전환
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish() // SplashActivity 종료
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "비밀번호 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "입력된 새 비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "공백란을 모두 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
