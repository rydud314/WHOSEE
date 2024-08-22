package com.example.seesaw

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Firebase
import com.google.firebase.dynamiclinks.androidParameters
import com.google.firebase.dynamiclinks.dynamicLink
import com.google.firebase.dynamiclinks.dynamicLinks
import com.google.firebase.dynamiclinks.iosParameters
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.net.URLEncoder


class Frag3_Share2 : Fragment() {

    private var view: View? = null
    private lateinit var qrCodeImage: ImageView
    private lateinit var cardImage: ImageView
    private lateinit var qrEditText: EditText
    private lateinit var generateQrButton: Button
    lateinit var card : Card


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

        val cardId = card.cardId.toString()

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
