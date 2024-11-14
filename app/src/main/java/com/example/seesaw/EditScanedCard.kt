package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityEditScanedCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditScanedCard : AppCompatActivity() {

    private lateinit var binding: ActivityEditScanedCardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    // 초기 힌트값을 저장하는 변수들
    private var initialHints: Map<String, String> = mapOf()
    private var currentText: MutableMap<String, String> = mutableMapOf()  // 현재 사용자 입력 내용 저장

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditScanedCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase 인스턴스 초기화
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // 인텐트로부터 데이터 가져오기
        val company = intent.getStringExtra("company") ?: ""
        val position = intent.getStringExtra("position") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val mobileNumber = intent.getStringExtra("mobileNumber") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val companyPhoneNumber = intent.getStringExtra("companyPhoneNumber") ?: ""
        val website = intent.getStringExtra("website") ?: ""

        // 초기 힌트값 설정
        initialHints = mapOf(
            "company" to company,
            "position" to position,
            "name" to name,
            "mobileNumber" to mobileNumber,
            "email" to email,
            "address" to address,
            "companyPhoneNumber" to companyPhoneNumber,
            "website" to website
        )

        // EditText 필드에 기본 힌트 설정 및 클릭 시 텍스트로 변환
        setHintAsText(binding.etCompany, company)
        setHintAsText(binding.etPosition, position)
        setHintAsText(binding.etName, name)
        setHintAsText(binding.etMobileNumber, mobileNumber)
        setHintAsText(binding.etEmail, email)
        setHintAsText(binding.etAddress, address)
        setHintAsText(binding.etCompanyPhone, companyPhoneNumber)
        setHintAsText(binding.etWebsite, website)

        // 수정 버튼 클릭 시 데이터 업데이트
        binding.btnEditCard.setOnClickListener {
            updateCard()
        }

        // 초기화 버튼 클릭 시 모든 EditText를 초기 힌트로 되돌림
        binding.btnReset.setOnClickListener {
            resetFields()
        }

        // 초기 상태 보기 버튼을 누르고 있는 동안 초기 힌트로 내용 표시
        binding.btnShowInitial.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 버튼을 눌렀을 때 현재 텍스트를 저장하고 초기 상태로 설정
                    saveCurrentText()
                    showInitialHints()
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // 버튼에서 손을 뗐을 때 수정된 내용 복원
                    restoreCurrentText()
                    true
                }
                else -> false
            }
        }
    }

    private fun setHintAsText(editText: EditText, hintText: String) {
        // 초기 힌트를 설정
        editText.hint = hintText

        // 포커스가 변경될 때 힌트를 텍스트로 변환
        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && editText.text.isEmpty()) {
                editText.setText(hintText)
                editText.hint = ""
            }
        }
    }

    private fun resetFields() {
        // 모든 EditText를 초기 힌트값으로 되돌림
        binding.etCompany.setText(initialHints["company"])
        binding.etPosition.setText(initialHints["position"])
        binding.etName.setText(initialHints["name"])
        binding.etMobileNumber.setText(initialHints["mobileNumber"])
        binding.etEmail.setText(initialHints["email"])
        binding.etAddress.setText(initialHints["address"])
        binding.etCompanyPhone.setText(initialHints["companyPhoneNumber"])
        binding.etWebsite.setText(initialHints["website"])

        Toast.makeText(this, "모든 필드가 초기화되었습니다.", Toast.LENGTH_SHORT).show()
    }

    private fun saveCurrentText() {
        // 현재 텍스트 필드의 값을 저장
        currentText["company"] = binding.etCompany.text.toString()
        currentText["position"] = binding.etPosition.text.toString()
        currentText["name"] = binding.etName.text.toString()
        currentText["mobileNumber"] = binding.etMobileNumber.text.toString()
        currentText["email"] = binding.etEmail.text.toString()
        currentText["address"] = binding.etAddress.text.toString()
        currentText["companyPhoneNumber"] = binding.etCompanyPhone.text.toString()
        currentText["website"] = binding.etWebsite.text.toString()
    }

    private fun showInitialHints() {
        // 초기 힌트값을 텍스트 필드에 설정
        binding.etCompany.setText(initialHints["company"])
        binding.etPosition.setText(initialHints["position"])
        binding.etName.setText(initialHints["name"])
        binding.etMobileNumber.setText(initialHints["mobileNumber"])
        binding.etEmail.setText(initialHints["email"])
        binding.etAddress.setText(initialHints["address"])
        binding.etCompanyPhone.setText(initialHints["companyPhoneNumber"])
        binding.etWebsite.setText(initialHints["website"])
    }

    private fun restoreCurrentText() {
        // 저장된 현재 텍스트 필드의 값으로 복원
        binding.etCompany.setText(currentText["company"])
        binding.etPosition.setText(currentText["position"])
        binding.etName.setText(currentText["name"])
        binding.etMobileNumber.setText(currentText["mobileNumber"])
        binding.etEmail.setText(currentText["email"])
        binding.etAddress.setText(currentText["address"])
        binding.etCompanyPhone.setText(currentText["companyPhoneNumber"])
        binding.etWebsite.setText(currentText["website"])
    }

    private fun updateCard() {
        val updatedData = mapOf(
            "company" to binding.etCompany.text.toString().trim(),
            "position" to binding.etPosition.text.toString().trim(),
            "name" to binding.etName.text.toString().trim(),
            "mobileNumber" to binding.etMobileNumber.text.toString().trim(),
            "email" to binding.etEmail.text.toString().trim(),
            "address" to binding.etAddress.text.toString().trim(),
            "companyPhoneNumber" to binding.etCompanyPhone.text.toString().trim(),
            "website" to binding.etWebsite.text.toString().trim()
        )

        firestore.collection("all_card_list").document(updatedData["company"].toString())
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "명함 수정 완료", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "명함 수정 실패: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
