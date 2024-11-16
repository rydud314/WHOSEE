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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Firebase
import com.google.firebase.dynamiclinks.androidParameters
import com.google.firebase.dynamiclinks.dynamicLink
import com.google.firebase.dynamiclinks.dynamicLinks
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class Frag3_Share2 : Fragment() {

    private var view: View? = null
    private lateinit var qrCodeImage: ImageView
    private lateinit var cardImage: ImageView
    private lateinit var qrEditText: EditText
    private lateinit var generateQrButton: Button
    private lateinit var btnSaveQr: ImageButton
    private lateinit var btnSaveImage : ImageButton
    private lateinit var tvCardName : TextView
    lateinit var card : Card

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

    companion object {
        fun newInstance(): Frag3_Share2 {
            return Frag3_Share2()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.activity_frag3_share2, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        var myCardList = intent.getSerializableExtra("myCardList") as ArrayList<Card>
//        loadCards(myCardList)


        arguments?.let {
            card = it.getParcelable("card")!!
        }
        Log.d(TAG, "프레그3 : card = ${card.cardId}")


        qrCodeImage = view.findViewById(R.id.show_qr)
        cardImage = view.findViewById(R.id.card_image)
        btnSaveQr = view.findViewById(R.id.save_qr)
        btnSaveImage = view.findViewById(R.id.save_image)
        tvCardName = view.findViewById(R.id.tv_card_name)

        val cardId = card.cardId.toString()
        tvCardName.text = card.name

        /* 예전 큐알 url 만드는 코드
        val qrCodeUrl = "https://whosee/Splash?cardId=$cardId"
        val encodedUrl = URLEncoder.encode(qrCodeUrl, "UTF-8")
        Log.d(TAG, "encode : $encodedUrl")

        // QR 코드 생성
        generateQRCode(qrCodeUrl)
        */


        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("https://whosee/Splash?cardId=$cardId")
            domainUriPrefix = "https://whosee.page.link"
            androidParameters {
                //("com.example.seesaw.android")
                fallbackUrl = Uri.parse("https://play.google.com/store/apps")
            }
        }
        val dynamicLinkUri = dynamicLink.uri
        Log.d(TAG, "dynamicLink : $dynamicLinkUri")

        // QR 코드 생성
        generateQRCode(dynamicLinkUri)

        // Glide를 사용하여 이미지 로드 및 적용
        loadCardImage(card.imageName)

        btnSaveQr.setOnClickListener{
            Log.d(TAG, "saveQR 버튼 클릭")
            val viewToCapture = view
            activityResultLauncher.launch(permissions)  // 권한 요청
            captureScreenshot(viewToCapture)
        }

        btnSaveImage.setOnClickListener{
            Log.d(TAG, "saveImage 버튼 클릭")
            val intent = Intent(requireContext(), ShareCardDetail::class.java)
            intent.putExtra("saveCardImage", cardId)


            startActivityForResult(intent, 2)
        }
        
    }

    private fun captureScreenshot(view: View){
        Log.d(TAG, "capture Screen 함수 IN")
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "버전 10 이상")
            saveScreenshotToGallery(requireContext(), bitmap)
        }
        else {
            Log.d(TAG, "버전 10 이하")
            saveScreenshotToGalleryLegacy(requireContext(), bitmap)
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
                    Toast.makeText(requireContext(), "QR코드가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()

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
            Toast.makeText(requireContext(), "QR코드가 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show()

            return uri
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    //다이나믹 링크 형식때무네 return 타입 Uri로 바꿈. 수정 시 참고
    private fun generateQRCode(text: Uri) {
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(text.toString(), BarcodeFormat.QR_CODE, 200, 200)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
                }
            }
            qrCodeImage.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun loadCardImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_profile_placeholder)
            .error(R.drawable.ic_profile_placeholder)
            .into(cardImage)
    }



}
