package com.example.seesaw

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern

class PreviewActivity : AppCompatActivity() {

    private lateinit var imageViewPreview: ImageView
    private lateinit var btnRetake: Button
    private var savedUri: Uri? = null
    private var executorService: ExecutorService? = null
    private var detectedTextView: TextView? = null

    private val ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 0)
        append(Surface.ROTATION_90, 90)
        append(Surface.ROTATION_180, 180)
        append(Surface.ROTATION_270, 270)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        executorService = Executors.newSingleThreadExecutor()
        setContentView(R.layout.activity_preview)

        imageViewPreview = findViewById(R.id.imageViewPreview)
        btnRetake = findViewById(R.id.btnRetake)

        savedUri = Uri.parse(intent.getStringExtra("photoUri"))
        imageViewPreview.setImageURI(savedUri)

        // 텍스트 추출 실행
        detectTextFromImage()
    }

    private fun detectTextFromImage() {
        executorService!!.execute {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, savedUri)
                detectTextFromBitmap(bitmap)
            } catch (e: IOException) {
                Log.e(TAG, "Error loading image: ${e.message}")
            }
        }
    }

    private fun detectTextFromBitmap(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, getRotationCompensation())
        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                extractTextFromResult(visionText)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Text detection failed: ${e.message}")
            }
    }

    private fun getRotationCompensation(): Int {
        val rotation = windowManager.defaultDisplay.rotation
        return ORIENTATIONS.get(rotation)
    }

    private fun extractTextFromResult(visionText: Text) {
        var company = ""
        var position = ""
        var name = ""
        var mobileNumber = ""
        var email = ""
        var address = ""
        var companyPhoneNumber = ""
        var website = ""

        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text.trim()

                when {
                    company.isEmpty() && (text.startsWith("회사") || text.contains("(주)") || text.contains("[주]")) -> company = text
                    position.isEmpty() && POSITION_SET.any { it in text } -> position = text
                    name.isEmpty() && (text.length == 3 || text.length == 4) && SURNAMES.contains(text[0].toString()) -> name = text
                    mobileNumber.isEmpty() && PHONE_PATTERN.matcher(text).find() || text.contains("010")-> mobileNumber = text
                    email.isEmpty() && EMAIL_PATTERN.matcher(text).find() || text.contains("@") -> email = text
                    address.isEmpty() && ADDRESS_PATTERN.matcher(text).find() -> address = text
                    companyPhoneNumber.isEmpty() && AREA_CODE_PATTERN.matcher(text).find() -> companyPhoneNumber = text
                    website.isEmpty() && WEBSITE_PATTERN.matcher(text).find() || text.contains("www") -> website = text
                }
            }
        }

        // EditScannedCard로 데이터 전달
        val intent = Intent(this, EditScanedCard::class.java).apply {
            putExtra("company", company)
            putExtra("position", position)
            putExtra("name", name)
            putExtra("mobileNumber", mobileNumber)
            putExtra("email", email)
            putExtra("address", address)
            putExtra("companyPhoneNumber", companyPhoneNumber)
            putExtra("website", website)
        }
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        executorService?.shutdown()
    }

    companion object {
        private const val TAG = "DetectText"

        private val POSITION_SET: Set<String> = setOf(
            "인턴", "사원", "주임", "대리", "과장", "차장", "부장", "팀장", "실장", "본부장", "이사",
            "상무", "전무", "부사장", "사장", "부회장", "회장"
        )
        private val SURNAMES: Set<String> = setOf(
            "김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오", "서",
            "신", "권", "황", "안", "송", "전", "홍", "유", "고", "문", "양", "손",
            "배", "백", "허", "남", "심", "노", "하", "곽", "성", "차", "주", "우"
        )

        private val PHONE_PATTERN: Pattern = Pattern.compile("\\b010[-\\s.]?\\d{4}[-\\s.]?\\d{4}\\b")
        private val AREA_CODE_PATTERN: Pattern = Pattern.compile(".*(02|031|032|033|041|042|043|044|051|052|053|054|055|061|062|063|064).*")
        private val EMAIL_PATTERN: Pattern = Pattern.compile("^(M|mail|E)[-\\s]?.*@\\w+\\.\\w+")
        private val WEBSITE_PATTERN: Pattern = Pattern.compile("^(www\\.)[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+")
        private val ADDRESS_PATTERN: Pattern = Pattern.compile(".*(서울|인천|부산|대구|대전|광주|울산|세종|경기|강원|충청북도|충북|충청남도|충남|전라북도|전북|전라남도|전남|경상북도|경북|경상남도|경남|제주|제주도).*")
    }
}
