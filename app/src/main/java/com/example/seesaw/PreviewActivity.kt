package com.example.seesaw

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.regex.Pattern


class PreviewActivity : AppCompatActivity() {

    private lateinit var imageViewPreview: ImageView
    private lateinit var btnRetake: Button
//    private lateinit var btnDetect: Button
    private var savedUri: Uri? = null

    private var executorService: ExecutorService? = null
    private var detectedTextView: TextView? = null
    private var mostRecentImageUri: Uri? = null


    // SparseIntArray for rotation compensation
    private val ORIENTATIONS = SparseIntArray().apply {
        append(Surface.ROTATION_0, 0)
        append(Surface.ROTATION_90, 90)
        append(Surface.ROTATION_180, 180)
        append(Surface.ROTATION_270, 270)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


//        // 권한 요청
//        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
//        }

        // ExecutorService 초기화
        executorService = Executors.newSingleThreadExecutor()
        // 레이아웃 설정
        setContentView(R.layout.activity_preview)



        imageViewPreview = findViewById(R.id.imageViewPreview)
        btnRetake = findViewById(R.id.btnRetake)
        //btnDetect = findViewById(R.id.btnDetect)

        savedUri = Uri.parse(intent.getStringExtra("photoUri"))
        imageViewPreview.setImageURI(savedUri)


        // 다시 찍기
        btnRetake.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }

        // 촬영된 이미지 저장
        savePhotoToGallery()

        // 가장 최근 사진에서 텍스트 추출 버튼
        val recentImageButton = findViewById<Button>(R.id.btnDetect)
        recentImageButton.setOnClickListener {
            Log.i(TAG, "Recent Image Button clicked!")

            loadMostRecentImage()
        }
    }


    private fun savePhotoToGallery() {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis()))
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/Camera")
            }
        }

        savedUri?.let { uri ->
            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)?.let {
                contentResolver.openOutputStream(it).use { outputStream ->
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        inputStream.copyTo(outputStream!!)
                    }
                }
            }
            Toast.makeText(this, "Photo saved to gallery", Toast.LENGTH_SHORT).show()
        }
    }

    // 갤러리의 가장 최근에 저장된 이미지를 불러와서 처리
    private fun loadMostRecentImage() {
        executorService!!.execute {
            mostRecentImageUri = getMostRecentImageUri()
            if (mostRecentImageUri != null) {
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(
                        contentResolver, mostRecentImageUri
                    )
                    detectTextFromBitmap(bitmap)
//                    val resultText = detectTextFromBitmap(bitmap)
//
//                    // 결과를 새로운 Activity로 전달
//                    val intent = Intent(this@PreviewActivity, PrintDetectedText::class.java)
//                    intent.putExtra("RESULT_TEXT", resultText)
//                    startActivity(intent)
                } catch (e: IOException) {
                    Log.e(TAG, "Error loading recent image: " + e.message)
                }
            } else {
                Log.e(TAG, "No recent image found")
            }
        }
    }

    // 갤러리에서 가장 최근에 저장된 이미지의 URI를 가져오는 함수
    private fun getMostRecentImageUri(): Uri? {
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC"
        contentResolver.query(uri, projection, null, null, sortOrder).use { cursor ->
            if (cursor != null && cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
            }
        }
        return null
    }

    private fun detectTextFromBitmap(bitmap: Bitmap) {
        // Adjust image orientation based on device rotation
        val image = InputImage.fromBitmap(bitmap, getRotationCompensation(this))
        // Korean 텍스트 인식기 초기화
        val recognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())

        //val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val resultText = extractTextFromResult(visionText)
                // 텍스트 인식 결과를 새로운 Activity로 전달
                val intent = Intent(this@PreviewActivity, PrintDetectedText::class.java)
                intent.putExtra("RESULT_TEXT", resultText)
                startActivity(intent)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Text detection failed: ${e.message}")
            }
    }

    private fun getRotationCompensation(context: Context): Int {
        val rotation = windowManager.defaultDisplay.rotation
        return ORIENTATIONS.get(rotation)
    }

    private fun extractTextFromResult(visionText: Text): String {
        val filteredText = StringBuilder()
        val rawText = StringBuilder() // 로우데이터를 저장할 변수
        val detectedFields = mutableSetOf<String>() // 중복 방지를 위한 Set

        // 필터링 항목의 중복 감지를 위한 플래그 설정
        var company = ""
        var position = ""
        var name = ""
        var mobileNumber = ""
        var email = ""
        var address = ""
        var companyPhoneNumber = ""
        var website = ""


        // 예제: 인식된 텍스트를 추출하고 필터링
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text.trim()
                Log.i(TAG, "Detected line: $text")

                // 중복 필터링: 이미 감지된 필드는 건너뜀
                if (detectedFields.contains(text)) continue

                // 로우 데이터에 추가 (필터링 여부와 관계없이 모든 텍스트를 추가)
                rawText.append(text).append("\n")

                // 필터링 로직
                when {
                    company.isEmpty() && (text.startsWith("회사") || text.startsWith("(주)")) -> {
                        company = text // 회사명 필터링 완료 표시
                    }
                    position.isEmpty() && POSITION_SET.any { it in text } -> {
                        position = text // 직급 필터링 완료 표시
                    }
                    name.isEmpty() && (text.length == 3 || text.length == 4) && SURNAMES.contains(text[0].toString()) -> {
                        name = text // 이름 필터링 완료 표시
                    }
                    mobileNumber.isEmpty() && PHONE_PATTERN.matcher(text).find() && "010" in text -> {
                        mobileNumber = text // 휴대전화 필터링 완료 표시
                    }
                    email.isEmpty() && EMAIL_PATTERN.matcher(text).find() -> {
                        email = text // 이메일 필터링 완료 표시
                    }
                    address.isEmpty() && ("주소" in text || text.matches(".*\\d{1,4}.*".toRegex())) -> {
                        address = text // 주소 필터링 완료 표시
                    }
                    companyPhoneNumber.isEmpty() && AREA_CODE_PATTERN.matcher(text).find() -> {
                        companyPhoneNumber = text // 회사 전화번호 필터링 완료 표시
                    }
                    website.isEmpty() && WEBSITE_PATTERN.matcher(text).find() -> {
                        website = text // 회사 웹사이트 필터링 완료 표시
                    }
                }

                // 중복 방지를 위해 필터링된 텍스트를 Set에 추가
                detectedFields.add(text)
            }
        }

        //return filteredText.toString()

        // 각 필터링 항목을 순서대로 추가, 데이터가 없으면 빈칸으로 출력
        filteredText.append("회사명: ").append(company).append("\n")
        filteredText.append("직급: ").append(position).append("\n")
        filteredText.append("이름: ").append(name).append("\n")
        filteredText.append("휴대전화: ").append(mobileNumber).append("\n")
        filteredText.append("이메일: ").append(email).append("\n")
        filteredText.append("주소: ").append(address).append("\n")
        filteredText.append("회사 전화번호: ").append(companyPhoneNumber).append("\n")
        filteredText.append("회사 웹사이트: ").append(website).append("\n")

        // 최종 결과물에 필터링된 데이터와 로우 데이터를 함께 포함
        return "|필터링되지 않은 원본 데이터|\n\n$rawText\n\n|필터링된 데이터|\n\n$filteredText"
    }




//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
//            val imageUri = data.data
//            try {
//                // URI에서 Bitmap 가져오기
//                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
//                // 비동기 작업으로 텍스트 감지 수행
//                executorService!!.execute {
//                    try {
//                        detectTextFromBitmap(bitmap)
//                    } catch (e: IOException) {
//                        Log.e(TAG, "Error during text detection: " + e.message)
//                    }
//                }
//            } catch (e: IOException) {
//                Log.e(TAG, "Error loading image: " + e.message)
//            }
//        }
//    }

//    @Throws(IOException::class)
//    fun detectTextFromBitmap(bitmap: Bitmap?): String {
//        Log.i(TAG, "Starting text detection")
//        if (bitmap == null) {
//            Log.e(TAG, "Error: Bitmap is null")
//            return ""
//        }
//
//        // Bitmap을 ByteString으로 변환
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
//        val byteArray = stream.toByteArray()
//        val imgBytes = com.google.protobuf.ByteString.copyFrom(byteArray)
//
//        // Google Vision API에 요청 준비
//        val requests: MutableList<AnnotateImageRequest> = ArrayList()
//        val img: Image = Image.newBuilder().setContent(imgBytes).build()
//        val feat: Feature = Feature.newBuilder().setType(Feature.Type.TEXT_DETECTION).build()
//        val request: AnnotateImageRequest =
//            AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build()
//        requests.add(request)
//
//        // assets 폴더에서 인증 파일 불러오기
//        val assetManager = assets
//        val credentialsStream = assetManager.open("whosee-438207-fa546ccff839.json")
//
//        // GoogleCredentials 객체를 생성하고 ImageAnnotatorSettings에 설정
//        val credentials: GoogleCredentials = GoogleCredentials.fromStream(credentialsStream)
//        val settings: ImageAnnotatorSettings = ImageAnnotatorSettings.newBuilder()
//            .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
//            .build()
//
//        // ImageAnnotatorClient 생성 및 요청 실행
//        try {
//            ImageAnnotatorClient.create(settings).use { client ->
//                Log.i(TAG, "Client created, sending request...")
//                val response: BatchAnnotateImagesResponse = client.batchAnnotateImages(requests)
//                val responses: List<AnnotateImageResponse> = response.responsesList
//
//                var companyName: String? = null
//                var position: String? = null
//                var name: String? = null
//                var mobileNumber: String? = null
//                var email: String? = null
//                var address: String? = null
//                var companyPhoneNumber: String? = null
//                var website: String? = null
//
//                for (res in responses) {
//                    if (res.hasError()) {
//                        Log.e(TAG, "Error: " + res.error.message)
//                        return ""
//                    }
//
//                    val lines: List<String> = res.textAnnotationsList[0].description.split("\n")
//                    for (text in lines) {
//                        Log.i(TAG, "Detected line: $text")
//                        if (companyName == null && (text.startsWith("회사") || text.startsWith("(주)"))) {
//                            companyName = text
//                        } else if (position == null && POSITION_SET.any { s -> text.contains(s) }) {
//                            position = text
//                        } else if (name == null && text.length == 3 && SURNAMES.contains(text.substring(0, 1))) {
//                            name = text
//                        } else if (mobileNumber == null && PHONE_PATTERN.matcher(text).find() && text.contains("010")) {
//                            mobileNumber = text
//                        } else if (email == null && EMAIL_PATTERN.matcher(text).find()) {
//                            email = text
//                        } else if (address == null && (text.contains("주소") || text.matches(".*\\d{1,4}.*".toRegex()))) {
//                            address = text
//                        } else if (companyPhoneNumber == null && AREA_CODE_PATTERN.matcher(text).find()) {
//                            companyPhoneNumber = text
//                        } else if (website == null && WEBSITE_PATTERN.matcher(text).find()) {
//                            website = text
//                        }
//                    }
//                }
//
//                val filteredText = StringBuilder()
//                filteredText.append("회사명 : ").append(companyName ?: "").append("\n")
//                filteredText.append("직급 : ").append(position ?: "").append("\n")
//                filteredText.append("이름 : ").append(name ?: "").append("\n")
//                filteredText.append("휴대전화 : ").append(mobileNumber ?: "").append("\n")
//                filteredText.append("이메일 : ").append(email ?: "").append("\n")
//                filteredText.append("주소 : ").append(address ?: "").append("\n")
//                filteredText.append("회사 전화번호(팩스) :").append(companyPhoneNumber ?: "").append("\n")
//                filteredText.append("회사 웹사이트 : ").append(website ?: "").append("\n")
//
//                return filteredText.toString()
//            }
//        } catch (e: Exception) {
//            Log.e(TAG, "Google Vision API call failed: " + e.message)
//            return ""
//        } finally {
//            credentialsStream.close()
//        }
//    }

    override fun onDestroy() {
        super.onDestroy()
        executorService?.shutdown()
    }

    companion object {
        private const val TAG = "DetectText"
        private const val PICK_IMAGE_REQUEST = 1

        private val POSITION_SET: Set<String> = setOf(
            "인턴", "사원", "주임", "대리", "과장", "차장", "부장", "팀장", "실장", "본부장", "이사",
            "상무", "전무", "부사장", "사장", "부회장", "회장"
        )
        private val SURNAMES: Set<String> = setOf(
            "김", "이", "박", "최", "정", "강", "조", "윤", "장", "임", "한", "오", "서",
            "신", "권", "황", "안", "송", "전", "홍", "유", "고", "문", "양", "손",
            "배", "백", "허", "남", "심", "노", "하", "곽", "성", "차", "주", "우"
        )

        private val PHONE_PATTERN: Pattern = Pattern.compile("\\b010[-\\s]?\\d{4}[-\\s]?\\d{4}\\b")
        private val AREA_CODE_PATTERN: Pattern = Pattern.compile(".*(02|031|032|033|041|042|043|044|051|052|053|054|055|061|062|063|064).*")
        private val EMAIL_PATTERN: Pattern = Pattern.compile("^(M|mail|E)[-\\s]?.*@\\w+\\.\\w+")
        private val WEBSITE_PATTERN: Pattern = Pattern.compile("^(www\\.)[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})+")
    }

}
