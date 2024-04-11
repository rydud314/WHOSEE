package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment

class Frag1_Home : Fragment() {

    // 이미지 소스 배열
    private val images = intArrayOf(R.drawable.ic_name_card_1, R.drawable.ic_name_card_2, R.drawable.ic_name_card_3, R.drawable.ic_name_card_4, R.drawable.ic_name_card_5)
    private var currentIndex = 0 // 현재 이미지 인덱스

    companion object{
        fun newInstance() : Frag1_Home{
            return Frag1_Home()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 레이아웃과 프래그먼트를 연결
        return inflater.inflate(R.layout.activity_frag1_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // findViewById는 이제 view에서 호출
        val imageView: ImageView = view.findViewById(R.id.iv_name_cards)
        val btn_left: Button = view.findViewById(R.id.btn_left)
        val btn_right: Button = view.findViewById(R.id.btn_right)

        val btn_camera: Button = view.findViewById(R.id.btn_camera)

        // 화면에 처음 로드될 때 첫 번째 이미지를 표시
        imageView.setImageResource(images[currentIndex])

        // 좌측 버튼 클릭 이벤트 처리
        btn_left.setOnClickListener {
            // currentIndex를 감소시키거나 마지막으로 이동
            currentIndex = if (currentIndex > 0) currentIndex - 1 else images.size - 1
            imageView.setImageResource(images[currentIndex])
        }

        // 우측 버튼 클릭 이벤트 처리
        btn_right.setOnClickListener {
            // currentIndex를 증가시키거나 처음으로 이동
            currentIndex = (currentIndex + 1) % images.size
            imageView.setImageResource(images[currentIndex])
        }

        btn_camera.setOnClickListener{
            val intent = Intent(context, CameraScan_1::class.java)
            startActivity(intent)
        }
    }
}
