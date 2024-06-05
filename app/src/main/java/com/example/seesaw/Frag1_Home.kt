//package com.example.seesaw
//
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.ImageView
//import androidx.fragment.app.Fragment
//
//class Frag1_Home : Fragment() {
//
//    // 이미지 소스 배열
//    private val images = intArrayOf(R.drawable.ic_name_card_1, R.drawable.ic_name_card_2, R.drawable.ic_name_card_3, R.drawable.ic_name_card_4, R.drawable.ic_name_card_5)
//    private var currentIndex = 0 // 현재 이미지 인덱스
//
//    companion object{
//        fun newInstance() : Frag1_Home{
//            return Frag1_Home()
//        }
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // 레이아웃과 프래그먼트를 연결
//        return inflater.inflate(R.layout.activity_frag1_home, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // findViewById는 이제 view에서 호출
//        val imageView: ImageView = view.findViewById(R.id.iv_name_cards)
////        val btn_left: Button = view.findViewById(R.id.btn_left)
////        val btn_right: Button = view.findViewById(R.id.btn_right)
//
//        val btn_camera: Button = view.findViewById(R.id.btn_camera_scan)
//
//        // 화면에 처음 로드될 때 첫 번째 이미지를 표시
//        imageView.setImageResource(images[currentIndex])
//
////        // 좌측 버튼 클릭 이벤트 처리
////        btn_left.setOnClickListener {
////            // currentIndex를 감소시키거나 마지막으로 이동
////            currentIndex = if (currentIndex > 0) currentIndex - 1 else images.size - 1
////            imageView.setImageResource(images[currentIndex])
////        }
////
////        // 우측 버튼 클릭 이벤트 처리
////        btn_right.setOnClickListener {
////            // currentIndex를 증가시키거나 처음으로 이동
////            currentIndex = (currentIndex + 1) % images.size
////            imageView.setImageResource(images[currentIndex])
////        }
//
//        btn_camera.setOnClickListener{
//            val intent = Intent(context, CameraScan_1::class.java)
//            startActivity(intent)
//        }
//    }
//}

package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class Frag1_Home : Fragment() {

    companion object {
        fun newInstance(): Frag1_Home {
            return Frag1_Home()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
// 레이아웃과 프래그먼트를 연결
        return inflater.inflate(R.layout.activity_frag1_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        // ViewPager2 설정
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        // 카드 데이터 목록
        val cards = listOf(
            NameCardData("UXUI Designer", "오지윤", "서울 특별시 성북구", "28세/여", "프리랜서", "knk0208@naver.com"),
            NameCardData("UXUI Designer", "김철수", "서울 특별시 강남구", "30세/남", "회사원", "chulsu@example.com"),
            NameCardData("Product Manager", "이영희", "부산광역시 해운대구", "27세/여", "회사원", "younghee@example.com")
            // 추가 카드 데이터...
        )

        // ViewPager2에 어댑터 설정
        viewPager.adapter = NameCardAdapter(cards)

        // TabLayout과 ViewPager2를 연결
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // 탭을 초기화하지 않음
        }.attach()

        // 명함 만들기 버튼 설정
        val btn_make_card: Button = view.findViewById(R.id.btn_make_card)
        btn_make_card.setOnClickListener {
            val intent = Intent(context, MakeCard::class.java)
            startActivity(intent)
        }

        //명함 편집 버튼 설정
        val btn_edit_card: Button = view.findViewById(R.id.btn_edit_card)
        btn_edit_card.setOnClickListener {
            val intent = Intent(context, EditCard::class.java)
            startActivity(intent)
        }

        // 카메라 버튼 설정
        val btn_camera: Button = view.findViewById(R.id.btn_camera_scan)
        btn_camera.setOnClickListener {
            val intent = Intent(context, CameraActivity::class.java)
            startActivity(intent)
        }

        // 리포트 보기 버튼 설정
        val btn_report: Button = view.findViewById(R.id.btn_open_report)
        btn_report.setOnClickListener {
            val intent = Intent(context, ChooseReport::class.java)
            startActivity(intent)
        }
    }
}

private fun AppCompatActivity.setSupportActionBar(toolbar: Toolbar) {

}
