package com.example.seesaw

import android.graphics.Bitmap
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.net.URLEncoder

class Frag3_Share : Fragment() {

    private lateinit var viewModel : CardViewModel

    private var view: View? = null
    private lateinit var qrCodeImage: ImageView
    private lateinit var qrEditText: EditText
    private lateinit var generateQrButton: Button
    val cardId = "bfuBfMhtRK"

    companion object {
        fun newInstance(): Frag3_Share {
            return Frag3_Share()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.activity_frag3_share, container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ViewModel 인스턴스 생성
        viewModel= ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CardViewModel::class.java)

        Log.d(TAG, "fra_share3: viewModel : ${viewModel.myCardList.size}")


        qrCodeImage = view.findViewById(R.id.show_qr)

        val qrCodeUrl = "whosee://sharelink/Splash?cardId=$cardId"
        val encodedUrl = URLEncoder.encode(qrCodeUrl, "UTF-8")
        Log.d(TAG, "encode : $encodedUrl")




        generateQRCode(qrCodeUrl)


        //qrEditText = view.findViewById(R.id.tv_qr)
        //generateQrButton = view.findViewById(R.id.btn_generate_qr)

        /*generateQrButton.setOnClickListener {
            val qrText = "KimNaKung\n@rlaskrud"
            qrEditText.setText(qrText)
            generateQRCode(qrText)
        }*/
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
}
