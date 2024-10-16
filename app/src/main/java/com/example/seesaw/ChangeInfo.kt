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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ChangeInfo : AppCompatActivity() {

    private lateinit var binding: ActivityChangeInfoBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()
        auth = Firebase.auth

        loadUserInfo()

        setupTextWatchers()

        binding.btnChange.setOnClickListener {
            updateUserInfo()
        }

        // 초기에 비밀번호 오류 메시지 숨기기
        binding.tvPwError.visibility = View.GONE
    }

    private fun loadUserInfo() {
        val user = auth.currentUser
        user?.let {
            val uid = user.uid
            firestore.collection("id_list").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("email") ?: "No ID"
                        val userPhone = document.getString("tel") ?: "No Phone Number"
                        binding.textView6.text = userName
                        binding.textView9.text = userPhone
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to load user info", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupTextWatchers() {
        binding.etPwNew.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePassword(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPwNewAgain.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Clear the error message when user starts typing
                binding.tvPwError.visibility = View.GONE
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                matchPasswords(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validatePassword(password: String): Boolean {
        return if (password.length < 6 || password.length > 10) {
            binding.tvPwHint.visibility = View.VISIBLE
            false
        } else {
            binding.tvPwHint.visibility = View.GONE
            true
        }
    }

    private fun matchPasswords(confirmPassword: String): Boolean {
        if (binding.etPwNew.text.toString() == confirmPassword) {
            binding.tvPwError.visibility = View.GONE
            return true
        } else {
            binding.tvPwError.visibility = View.VISIBLE
            return false
        }
    }

    private fun updateUserInfo() {
        val pwNew = binding.etPwNew.text.toString().trim()
        val pwNewAgain = binding.etPwNewAgain.text.toString().trim()

        if (!validatePassword(pwNew) || !matchPasswords(pwNewAgain)) {
            Toast.makeText(this, "비밀번호 요구사항을 확인해 주세요", Toast.LENGTH_SHORT).show()
            return
        }

        if (pwNew.isNotBlank()) {
            val user = auth.currentUser
            user?.let {
                user.updatePassword(pwNew)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            updateFirestorePassword(user.uid, pwNew)
                        } else {
                            Toast.makeText(this, "Failed to update password.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun updateFirestorePassword(uid: String, password: String) {
        firestore.collection("id_list").document(uid).update("pw", password)
            .addOnSuccessListener {
                Toast.makeText(this, "비밀번호가 변경되었습니다.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update Firestore.", Toast.LENGTH_SHORT).show()
            }
    }
}