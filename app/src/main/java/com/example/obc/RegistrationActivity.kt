package com.example.obc

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.example.obc.databinding.ActivityLoginBinding
import com.example.obc.databinding.ActivityRegistrationBinding
import com.example.obc.model.Friend
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


private lateinit var auth: FirebaseAuth
lateinit var database: DatabaseReference
private lateinit var binding: ActivityRegistrationBinding

@Suppress("DEPRECATION")
class RegistrationActivity: AppCompatActivity() {
    private var imageUri : Uri? = null
    //이미지 등록
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if(result.resultCode == RESULT_OK) {
                imageUri = result.data?.data //이미지 경로 원본
                binding.userImage.setImageURI(imageUri) //이미지 뷰를 바꿈
                Log.d("이미지", "성공")
            }
            else{
                Log.d("이미지", "실패")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        auth = Firebase.auth
        database = Firebase.database.reference

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)

        val userEmail = binding.userEmail.text
        val userPassword = binding.userPassword.text
        val userName = binding.userName.text
        val registrationBtn = binding.registrationBtn
        val userImage = binding.userImage
        var profileCheck = false

        //사진 등록
        userImage.setOnClickListener{
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
            profileCheck = true
        }

        val intent = Intent(this, LoginActivity::class.java)

        registrationBtn.setOnClickListener {
            if(userEmail.isEmpty() || userPassword.isEmpty() || userName.isEmpty() || !profileCheck)  {
                Toast.makeText(this, "아이디, 비밀번호, 이름, 프로필 사진을 제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if(!userEmail.contains(".com", ignoreCase = false)){
                Toast.makeText(this, "아이디를 이메일 형식에 맞게 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if(userPassword.length<6){
                Toast.makeText(this, "비밀번호를 6자 이상으로 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else{
                if(!profileCheck){
                    Toast.makeText(this, "프로필사진을 등록해주세요.", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "생성중", Toast.LENGTH_SHORT).show()
                    auth.createUserWithEmailAndPassword(userEmail.toString().trim(), userPassword.toString().trim()).addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user = Firebase.auth.currentUser
                                val userId = user?.uid
                                val userUid = userId.toString()
                                FirebaseStorage.getInstance().reference.child("userImages").child("$userUid/photo").putFile(imageUri!!).addOnSuccessListener {
                                            var userProfile: Uri? = null
                                            FirebaseStorage.getInstance().reference.child("userImages").child("$userUid/photo").downloadUrl
                                                    .addOnSuccessListener {
                                                        userProfile = it
                                                        Log.d("이미지 URL", "$userProfile")

                                                        var firestore = FirebaseFirestore.getInstance()
                                                        val userData = hashMapOf(
                                                            "userEmail" to userEmail.toString(),
                                                            "userName" to userName.toString(),
                                                            "userProfileImage" to userProfile.toString(),
                                                            "userUid" to userUid.toString()
                                                        )
                                                        firestore.collection("UserInfo").document(userEmail.toString()).set(userData, SetOptions.merge())

                                                        val friend = Friend(userEmail.toString(), userName.toString(), userProfile.toString(), userUid)
                                                        database.child("users").child(userId.toString()).setValue(friend)
                                                        
                                                    }
                                        }
                                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                Log.e(TAG, "$userId")
                                finish()
                                startActivity(intent)
                            }
                            else {
                                Log.e(TAG, "회원가입 실패", task.exception)
                                Toast.makeText(this, "등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
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

    companion object {
        private const val TAG = "EmailPassword"
    }
}