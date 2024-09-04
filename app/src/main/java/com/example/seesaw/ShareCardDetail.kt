package com.example.seesaw

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.seesaw.databinding.ActivityShareCardDetailBinding
import com.example.seesaw.databinding.NameCardDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val PACKAGE_NAME = "com.example.seesaw"

class ShareCardDetail : AppCompatActivity() {

    private lateinit var binding: ActivityShareCardDetailBinding
    private lateinit var detailBinding: NameCardDetailBinding
    private lateinit var frag3Share: Frag3_Share2
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var cardId : String

    private var permissions = if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU){
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
    else{
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    //권한 허용 검사 결과
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ resultMap ->
        val isAllGranted = permissions.all{e -> resultMap[e] == true}
        if(isAllGranted){
            Log.d(TAG, "모든 권한 모두 부여 완료")
        }

        if(resultMap[Manifest.permission.READ_MEDIA_IMAGES] == true){
            Log.d(TAG, "이미지 권한 부여 완료 완료")
        }

        if(resultMap[Manifest.permission.READ_EXTERNAL_STORAGE] == true){
            Log.d(TAG, "저장소 읽기 권한 부여 완료")
        }
        if(resultMap[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true){
            Log.d(TAG, "저장소 입력 권한 부여 완료")
        }
        else{
            Log.d(TAG, "모든 권한 부여 실패")
        }
    }

    //인텐트 수신코드 결과
    private val activityIntentResultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){

        // SubOne에서 결과를 받아옴
        if(it.resultCode == 1){
            val intent = it.data
            cardId = intent!!.getStringExtra("uriExist").toString()
            cardId = cardId!!.replace("[", "")
            cardId = cardId!!.replace("]", "")
            Log.d(TAG, "shareDetail : uriCardID = $cardId")
        }

        // SubTwo에서 결과를 받아옴
        else if(it.resultCode == 2){
            val intent = it.data
            cardId = intent!!.getStringExtra("saveCardImage").toString()
            Log.d(TAG, "shareDetail : saveImageCardId = $cardId")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareCardDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        //uid가 유효하지 않을 때
        if (uid == null) {
            Log.d(TAG, "shareDetail : uid없음")
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()

        }
        //uid가 유효할 때
        else {
            // 포함된 레이아웃 바인딩 초기화
            detailBinding =
                NameCardDetailBinding.bind(binding.root.findViewById(R.id.shared_name_card_detail_container))

            // 인텐트로부터 데이터 가져오기
            var uriCard = intent.getSerializableExtra("uriExist").toString()
            var saveImageCard = intent.getSerializableExtra("saveCardImage").toString()

            uriCard?.let {
                cardId = uriCard
                cardId = cardId!!.replace("[", "")
                cardId = cardId!!.replace("]", "")
                Log.d(TAG, "shareDetail : uriCardID = $cardId")
            }
            saveImageCard?.let {
                cardId = saveImageCard
                Log.d(TAG, "shareDetail : saveImageCardId = $cardId")
            }

            // 자신의 카드 정보 가져오기
            firestore.collection("all_card_list").whereEqualTo("cardId", cardId).get()
                .addOnSuccessListener { querySnapshot ->
                    val documents = querySnapshot.documents.toMutableList()
                    Log.d(ContentValues.TAG, "querysnapshot complete")

                    if (documents != null && documents.isNotEmpty()) {
                        Log.d(ContentValues.TAG, "result.document is not null")

                        for (document in documents) {
                            val cardId = document["cardId"].toString()
                            val name = document["name"].toString()
                            val workplace = document["workplace"].toString()
                            val introduction = document["introduction"].toString()
                            val email = document["email"].toString()
                            val position = document["position"].toString()
                            val gender = document["gender"].toString()
                            val imageName = document["imageName"].toString()
                            val job = document["job"].toString()
                            val pofol = document["pofol"].toString()
                            val sns = document["sns"].toString()
                            val tel = document["tel"].toString()

                            Log.d(ContentValues.TAG, "cardData => $name($cardId)")

                            // 뷰에 데이터 설정
                            binding.tvName.text = name
                            //binding.tvJob.text = "Job: " + it.job
                            //binding.tvIntroduction.text = "Introduction: " + introduction
                            //binding.tvWorkplace.text = "Workplace: " + it.workplace
                            //binding.tvGender.text = "Gender: " + it.gender
                            //binding.tvPosition.text = "Position: " + it.position
                            binding.tvTel.text = tel
                            //binding.tvEmail.text = "Email: " + it.email
                            binding.tvSns.text = "SNS: " + sns
                            binding.tvPortfolio.text = "Portfolio: " + pofol

                            // 이미지 설정
                            loadCardImage(imageName)

                            // 뷰에 데이터 설정
                            detailBinding.tvName.text = name
                            detailBinding.tvJob.text = job
                            detailBinding.tvIntroduction.text = introduction
                            detailBinding.tvWorkplace.text = workplace
                            detailBinding.tvGender.text = gender
                            detailBinding.tvPosition.text = position
                            //detailBinding.tvTel.text = "Tel : " + it.tel
                            detailBinding.tvEmail.text = email
//            detailBinding.tvSns.text = "SNS : " + it.sns
//            detailBinding.tvPortfolio.text = "Portfolio : " + it.pofol

                            // 이미지 설정
                            loadCardImage(imageName)
                        }
                    }

                    // 명함 저장 버튼 클릭 리스너 설정
                    binding.btnSaveCard.setOnClickListener {
                        if (uid != null) {
                            cardId?.let { it -> saveToMyCardList(uid, it) }
                        }
                    }

                    // 갤러리 저장 버튼 클릭 리스너 설정
                    binding.btnSaveCardImage.setOnClickListener {
                        if (uid != null) {
                            saveToGallery(binding.root.rootView)
                        }
                    }
                }

        }
    }

    private fun saveToGallery(view: View) {
        activityResultLauncher.launch(permissions)  // 권한 요청
        if (view != null) {
            captureScreenshot(view)
        }
    }

    private fun loadCardImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(detailBinding.cardImage)
    }

    private fun saveToMyCardList(userId: String, cardId: String) {
        val checkRef = firestore.collection("my_card_list").document(userId)
        checkRef.get().addOnSuccessListener { doc ->

            if (doc.exists()) {
                // 문서가 존재하면
                val existingArray = doc.get("cards") as? ArrayList<HashMap<String, Any>>
                if (existingArray != null) {
                    // 배열이 이미 존재하면

                    // 내 명함인지 확인
                    var isExisted = false
                    for (i in existingArray) {
                        if (i.containsValue(cardId)) {
                            isExisted = true
                            break
                        }
                    }
                    if (!isExisted) {
                        //명함 공유 받기 가능 -> 내 명함이 아니라는 뜻
                        //wallet_list에 저장하는 코드
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
                                        Toast.makeText(this, "이미 저장한 명함입니다.", Toast.LENGTH_SHORT)
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
                            Log.d(TAG, "명함이 저장되었습니다.")
                            Toast.makeText(this, "명함을 저장하였습니다.", Toast.LENGTH_SHORT).show()
                            finishCreation()
                        }.addOnFailureListener { e ->
                            Log.d(TAG, "명함 저장 실패")
                        }

                    } else {
                        // 공유 받은 명함이 내 명함일 때
                        Toast.makeText(this, "나의 명함은 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 배열이 존재하지 않으면
                    Log.d(TAG, "내 명함 체크 실패")
                }
            }
        }.addOnFailureListener { e ->
            Log.d(TAG, "내 명함 체크 실패(failureListener)")
        }

    }

    private fun captureScreenshot(view: View){
        Log.d(TAG, "capture Screen 함수 IN")
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "버전 10 이상")
            saveScreenshotToGallery(this, bitmap)
        }
        else {
            Log.d(TAG, "버전 10 이하")
            saveScreenshotToGalleryLegacy(this, bitmap)
        }
    }

    // 안드로이드 10 이상
    private fun saveScreenshotToGallery(context: Context, bitmap: Bitmap): Uri? {
        val filename = "screenshot_${System.currentTimeMillis()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Screenshots")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(uri).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    Toast.makeText(this, "명함이 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()

                }
            }
        }

        return uri
    }

    // 안드로이드 10 미만
    private fun saveScreenshotToGalleryLegacy(context: Context, bitmap: Bitmap): Uri? {
        val filename = "screenshot_${System.currentTimeMillis()}.png"
        val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/Screenshots")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, filename)
        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            val uri = Uri.fromFile(file)
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            intent.data = uri
            context.sendBroadcast(intent)
            Toast.makeText(this, "명함이 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()

            return uri
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
    private fun finishCreation() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
