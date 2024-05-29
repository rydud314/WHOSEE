package com.example.obc


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.obc.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase


private lateinit var auth: FirebaseAuth
private lateinit var binding: ActivityLoginBinding

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        supportActionBar?.hide()


        val email = binding.etLoginId
        val password = binding.etLoginPassword

        val loginBtn = binding.profileButton
        val registrationBtn = binding.btnRegistration

        //로그인 버튼 클릭시
        loginBtn.setOnClickListener{
            if(email.text.isEmpty() || password.text.isEmpty())
                Toast.makeText(this, "아이디 또는 비밀번호가 비어 있습니다.", Toast.LENGTH_SHORT).show()
            else
                signIn(email.text.toString(), password.text.toString())
        }

        //회원 가입 버튼 클릭시
        registrationBtn.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }
    //로그인 확인
    private fun signIn(email: String, password: String) {

        val sharedPreferences = this.getSharedPreferences("other", 0)
        val editor = sharedPreferences.edit()

        val intentMain = Intent(this, MainActivity::class.java)

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("로그인", "성공")
                        finish()
                        editor.putString("userEmail",email.toString())
                        editor.apply()
                        startActivity(intentMain)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(this, "정확한 아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                        Log.d("로그인", "실패")
                    }
                }
    }



    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            reload();
        }
    }

    private fun reload() {

    }
}

