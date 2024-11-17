package com.example.seesaw

import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.service.controls.ControlsProviderService
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.seesaw.databinding.ActivityEditScanedCardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditScanedCard : AppCompatActivity() {

    private lateinit var binding: ActivityEditScanedCardBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var cardId: String = ""

    // 선택된 성별 상태를 저장하는 변수
    private var selectedGender: String? = null

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

        // "남" 버튼 클릭 리스너 설정
        binding.btnMale.setOnClickListener {
            handleGenderSelection("남", binding.btnMale, binding.btnFemale)
        }

        // "여" 버튼 클릭 리스너 설정
        binding.btnFemale.setOnClickListener {
            handleGenderSelection("여", binding.btnFemale, binding.btnMale)
        }

        // 1.8:1 비율에 따라 크롭된 이미지 로드 (추가 크롭 적용)
       loadCroppedImageWithExtraCrop()

        // 인텐트로부터 데이터 가져오기
        val company = intent.getStringExtra("company") ?: ""
        val position = intent.getStringExtra("position") ?: ""
        val name = intent.getStringExtra("name") ?: ""
        val mobileNumber = intent.getStringExtra("mobileNumber") ?: ""
        val email = intent.getStringExtra("email") ?: ""
        //val address = intent.getStringExtra("address") ?: ""
        //val companyPhoneNumber = intent.getStringExtra("companyPhoneNumber") ?: ""
        val website = intent.getStringExtra("website") ?: ""

        val gender = ""

        // 초기 힌트값 설정
        initialHints = mapOf(
            "company" to company,
            "position" to position,
            "name" to name,
            "mobileNumber" to mobileNumber,
            "email" to email,
            "성별" to gender,
            //"address" to address,
            //"companyPhoneNumber" to companyPhoneNumber,
            "website" to website
        )

        // EditText 필드에 기본 힌트 설정 및 클릭 시 텍스트로 변환
        setHintAsText(binding.etCompany, company)
        setHintAsText(binding.etPosition, position)
        setHintAsText(binding.etJob, position)
        setHintAsText(binding.etName, name)
        setHintAsText(binding.etMobileNumber, mobileNumber)
        setHintAsText(binding.etEmail, email)
        //setHintAsText(binding.etAddress, address)
        //setHintAsText(binding.etCompanyPhone, companyPhoneNumber)
        setHintAsText(binding.etWebsite, website)

        // 저장 버튼 클릭 시 데이터 업데이트
        binding.btnSaveScannedCard.setOnClickListener {
            saveAllHintsAsText() // 힌트를 텍스트로 변환

            // 메모 입력 팝업 띄우기
            showAddMemoDialog { memo ->
                // 메모를 introduction 필드로 저장
                updateCard(memo)
            }
        }

        // 초기화 버튼 클릭 시 모든 EditText를 초기 힌트로 되돌림
        binding.btnReset.setOnClickListener {
            resetFields()
        }

        // 다시찍기 버튼 클릭 시 모든 EditText를 초기 힌트로 되돌림
        binding.btnRetake.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
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

    // 성별 버튼 상태를 처리하는 함수
    private fun handleGenderSelection(
        gender: String,
        selectedButton: Button,
        otherButton: Button
    ) {
        if (selectedGender == gender) {
            // 이미 선택된 상태라면 선택 해제
            selectedGender = null
            binding.tvGender.text = "" // 성별 필드 비우기
            binding.tvGender.hint = "성별" // 힌트 복원
            resetGenderButtons() // 버튼 상태 초기화
        } else {
            // 선택되지 않은 상태라면 선택
            selectedGender = gender // 현재 선택된 성별 저장
            binding.tvGender.text = selectedGender // 선택된 성별을 텍스트로 표시
            binding.tvGender.hint = "" // 힌트 제거

            // 버튼 색상 업데이트
            selectedButton.setBackgroundResource(R.drawable.selected_button_background)
            otherButton.setBackgroundResource(R.drawable.default_button_background)
        }
    }

    // 버튼 색상 초기화 함수
    private fun resetGenderButtons() {
        binding.btnMale.setBackgroundResource(R.drawable.default_button_background)
        binding.btnFemale.setBackgroundResource(R.drawable.default_button_background)
    }


    private fun showAddMemoDialog(onMemoSaved: (String) -> Unit) {
        // 팝업 레이아웃을 inflate
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_memo, null)
        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        // 팝업 내부 요소
        val etMemo = dialogView.findViewById<EditText>(R.id.etMemo)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnDialogCancel)
        val btnSave = dialogView.findViewById<Button>(R.id.btnDialogSave)

        // "이전" 버튼 클릭 이벤트
        btnCancel.setOnClickListener {
            dialog.dismiss() // 팝업 닫기
        }

        // "다음" 버튼 클릭 이벤트
        btnSave.setOnClickListener {
            val memo = etMemo.text.toString().trim()
            onMemoSaved(memo) // 입력된 메모 전달
            dialog.dismiss() // 팝업 닫기

            // 저장 완료 화면으로 이동
            val intent = Intent(this, SaveCompleteActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }

        dialog.show()
    }


    // 갤러리에서 최근 이미지를 가져오고 1.8:1 비율로 크롭하여 ImageView에 설정
    private fun loadCroppedImageWithExtraCrop() {
        val bitmap = loadLatestImageWithoutPermission(contentResolver)
        if (bitmap != null) {
            try {
                val extraCrop = 150 // 추가 크롭값 (픽셀)
                val croppedBitmap = cropBitmapToRatio(bitmap, 18, 10, extraCrop) // 1.8:1 비율로 추가 크롭
                binding.imageViewPreview.setImageBitmap(croppedBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "이미지를 크롭할 수 없습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "최근 이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
        }
    }


    // 갤러리에서 가장 최근 이미지를 가져오는 함수
    private fun loadLatestImageWithoutPermission(contentResolver: ContentResolver): Bitmap? {
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_ADDED
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            if (it.moveToFirst()) {
                val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val imageId = it.getLong(idColumn)

                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    imageId.toString()
                )

                return try {
                    val inputStream = contentResolver.openInputStream(contentUri)
                    BitmapFactory.decodeStream(inputStream)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
        return null
    }

    // Bitmap을 비율(1.8:1)로 크롭하는 함수
    private fun cropBitmapToRatio(
        bitmap: Bitmap,
        aspectRatioWidth: Int,
        aspectRatioHeight: Int,
        extraCrop: Int = 0 // 추가로 잘라낼 픽셀 값
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // 비율에 따라 크롭 영역 계산
        val targetWidth: Int
        val targetHeight: Int

        if (width.toFloat() / height > aspectRatioWidth.toFloat() / aspectRatioHeight) {
            // 이미지가 비율보다 더 넓을 경우, 높이를 기준으로 너비 계산
            targetHeight = height - 2 * extraCrop
            targetWidth = (targetHeight * aspectRatioWidth / aspectRatioHeight)
        } else {
            // 이미지가 비율보다 더 좁을 경우, 너비를 기준으로 높이 계산
            targetWidth = width - 2 * extraCrop
            targetHeight = (targetWidth * aspectRatioHeight / aspectRatioWidth)
        }

        // 크롭 시작 위치 (중앙 정렬)
        val startX = (width - targetWidth) / 2
        val startY = (height - targetHeight) / 2

        // 크롭 영역이 유효한지 확인
        if (targetWidth <= 0 || targetHeight <= 0) {
            throw IllegalArgumentException("추가 크롭 값이 너무 커서 이미지가 잘리지 않습니다.")
        }

        // 크롭된 Bitmap 반환
        return Bitmap.createBitmap(bitmap, startX, startY, targetWidth, targetHeight)
    }



    private fun setHintAsText(editText: TextView, hintText: String) {
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
        //binding.etAddress.setText(initialHints["address"])
        //binding.etCompanyPhone.setText(initialHints["companyPhoneNumber"])
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
        //currentText["address"] = binding.etAddress.text.toString()
        //currentText["companyPhoneNumber"] = binding.etCompanyPhone.text.toString()
        currentText["website"] = binding.etWebsite.text.toString()
    }

    private fun showInitialHints() {
        // 초기 힌트값을 텍스트 필드에 설정
        binding.etCompany.setText(initialHints["company"])
        binding.etPosition.setText(initialHints["position"])
        binding.etName.setText(initialHints["name"])
        binding.etMobileNumber.setText(initialHints["mobileNumber"])
        binding.etEmail.setText(initialHints["email"])
        //binding.etAddress.setText(initialHints["address"])
        //binding.etCompanyPhone.setText(initialHints["companyPhoneNumber"])
        binding.etWebsite.setText(initialHints["website"])
    }

    private fun restoreCurrentText() {
        // 저장된 현재 텍스트 필드의 값으로 복원
        binding.etCompany.setText(currentText["company"])
        binding.etPosition.setText(currentText["position"])
        binding.etJob.setText(currentText["job"])
        binding.etName.setText(currentText["name"])
        binding.etMobileNumber.setText(currentText["mobileNumber"])
        binding.etEmail.setText(currentText["email"])
        //binding.etAddress.setText(currentText["address"])
        //binding.etCompanyPhone.setText(currentText["companyPhoneNumber"])
        binding.etWebsite.setText(currentText["website"])
    }

    private fun updateCard(memo: String) {

        val website = binding.etWebsite.text.toString().trim()

        val name = binding.etName.text.toString().trim()
        val job = binding.etJob.text.toString().trim()
        //val introduction = binding.etIntroduction.text.toString().trim()
        val company = binding.etCompany.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val gender = binding.tvGender.text.toString().trim()
        val position = binding.etPosition.text.toString().trim()
        val mobileNumber = binding.etMobileNumber.text.toString().trim()
        //val sns = binding.etSns.text.toString().trim()
        //val pofol = binding.etPortfolio.text.toString().trim()


        if (cardId == "") {
            cardId = getRandomString(10)
        }

        Log.d(TAG, "cardId =  $cardId")

        if (name.isNullOrEmpty()) {
            // 이름이 비어있을 경우 경고 메시지 표시
            Toast.makeText(this, "이름은 필수 입력사항입니다.", Toast.LENGTH_SHORT).show()
        } else {
            val currentUser = auth.currentUser
            val uid = currentUser?.uid

            uid?.let { userId ->
                val cardRef = firestore.collection("all_card_list").document(cardId)

                val cardData = hashMapOf<String, Any>(
                    "cardId" to cardId,
                    "name" to name,
                    "job" to position,
                    "introduction" to memo,
                    "workplace" to company,
                    "email" to email,
                    "gender" to gender,
                    "position" to position,
                    "tel" to mobileNumber,
                    "sns" to "",
                    "pofol" to website,
                    "imageName" to ""
                )

                cardRef.set(cardData).addOnSuccessListener {
                    Log.d(TAG, "명함이 생성되었습니다.")
                    saveToMyCardList(userId, cardId)
                }.addOnFailureListener {
                    Log.d(TAG, "명함 생성 실패")
                }

            }
        }
    }

    private fun saveToMyCardList(userId: String, cardId: String) {

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
                        Toast.makeText(this, "이미 존재하는 cardId", Toast.LENGTH_SHORT)
                            .show()
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
            Log.d(ControlsProviderService.TAG, "명함이 저장되었습니다.")
            Toast.makeText(this, "명함을 저장하였습니다.", Toast.LENGTH_SHORT).show()
            //finishCreation()
        }.addOnFailureListener { e ->
            Log.d(ControlsProviderService.TAG, "명함 저장 실패")
        }

    }

    private fun finishCreation() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun getRandomString(length: Int): String {
        val charset = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length).map { charset.random() }.joinToString("")
    }

    private fun saveAllHintsAsText() {
        val editTexts = listOf(
            binding.etCompany,
            binding.etPosition,
            binding.etJob,
            binding.etName,
            binding.etMobileNumber,
            binding.etEmail,
            binding.etWebsite
        )

        // 각 EditText의 텍스트가 비어 있으면 힌트를 텍스트로 설정
        editTexts.forEach { editText ->
            if (editText.text.isEmpty()) {
                editText.setText(editText.hint)
            }
        }
    }



}
