package com.example.seesaw

import android.graphics.Bitmap
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.net.URLEncoder

class Frag3_Share2 : Fragment() {

    private var view: View? = null
    private lateinit var qrCodeImage: ImageView
    private lateinit var cardImage: ImageView
    lateinit var card: Card

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

        arguments?.let {
            card = it.getParcelable("card")!!
        }
        Log.d(TAG, "프레그3 : card = ${card.cardId}")

        qrCodeImage = view.findViewById(R.id.show_qr)
        cardImage = view.findViewById(R.id.card_image)

        // 카드 ID를 사용하여 QR 코드 URL 생성
        val cardId = card.cardId.toString()
        val qrCodeUrl = "whosee://sharelink/Splash?cardId=$cardId"
        val encodedUrl = URLEncoder.encode(qrCodeUrl, "UTF-8")
        Log.d(TAG, "encode : $encodedUrl")

        // QR 코드 생성
        generateQRCode(qrCodeUrl)

        // Glide를 사용하여 이미지 로드 및 적용
        loadCardImage(card.imageName)
    }

    private fun generateQRCode(text: String) {
        val qrCodeWriter = QRCodeWriter()
        try {
            val bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200)
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
