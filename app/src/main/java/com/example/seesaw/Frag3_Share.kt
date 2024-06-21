package com.example.seesaw

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

class Frag3_Share : Fragment() {

    private var view: View? = null
    private lateinit var qrCodeImage: ImageView
    private lateinit var qrEditText: EditText
    private lateinit var generateQrButton: Button

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

        qrCodeImage = view.findViewById(R.id.qr_code_image)
        qrEditText = view.findViewById(R.id.tv_qr)
        generateQrButton = view.findViewById(R.id.btn_generate_qr)

        generateQrButton.setOnClickListener {
            val qrText = "KimNaKung\n@rlaskrud"
            qrEditText.setText(qrText)
            generateQRCode(qrText)
        }
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
