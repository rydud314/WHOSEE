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

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.getField

class Frag1_Home : Fragment() {

    companion object {
        fun newInstance(): Frag1_Home {
            return Frag1_Home()
        }
    }

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

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

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val uid = currentUser?.uid.toString()
        Log.d(TAG, "UID =  $uid")

        var cardId = ""
        var card_list = mutableListOf<String>()

        val myCardList : MutableList<Card> = mutableListOf()
        myCardList.clear()

        if (uid != null) {
            firestore.collection("my_card_list").document(uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        val cards = document.get("cards") as? List<Map<String, Any>>
                        if (cards != null) {
                            for (card in cards) {
                                cardId = card["cardId"].toString()
                                Log.d(TAG, "cardId = $cardId")
                                card_list.add(cardId)

                            }

                            //자신의 카드 정보 가져오기
                            firestore.collection("all_card_list").whereIn("cardId", card_list).get().addOnSuccessListener { querySnapshot ->
                                    val documents = querySnapshot.documents.toMutableList()
                                    Log.d(TAG, "querysnapshot complete")

                                    if (documents != null && documents.isNotEmpty()){
                                        Log.d(TAG, "result.document is not null")

                                        for (document in documents){
                                            val cardId = document["cardId"].toString()
                                            val name = document["name"].toString()
                                            val workplace = document["workplace"].toString()
                                            val introduction = document["introduction"].toString()
                                            val email = document["email"].toString()
                                            val position = document["position"].toString()
                                            val gender  = document["gender"].toString()
                                            val imageName= document["imageName"].toString()
                                            val job = document["job"].toString()
                                            val pofol = document["pofol"].toString()
                                            val sns = document["sns"].toString()
                                            val tel = document["tel"].toString()

                                            Log.d(TAG, "cardData => $name($cardId)")
                                            myCardList.add(Card(name, position, workplace, email, cardId, gender, imageName, introduction, job, pofol, sns, tel))
                                        }
                                    }
                                Log.d(TAG, "${myCardList.size}")
                                // ViewPager2에 어댑터 설정
                                viewPager.adapter = NameCardAdapter(myCardList)

                                // TabLayout과 ViewPager2를 연결
                                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                                    // 탭을 초기화하지 않음
                                }.attach()

                                }.addOnFailureListener{Log.d(TAG, "querysnapshot 실패")}

                        } else {
                            Log.d(TAG, "보유한 명함이 없습니다.")
                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.exception)
                }
            }
        }
        else{
            Log.d(TAG, "when check the my card, uid is null")
        }

        Log.d(TAG, "second : ${myCardList.size}")

        /*
        // 카드 데이터 목록
        val cards = listOf(
            NameCardData("UXUI Designer", "오지윤", "서울 특별시 성북구", "28세/여", "프리랜서", "knk0208@naver.com"),
            NameCardData("UXUI Designer", "김철수", "서울 특별시 강남구", "30세/남", "회사원", "chulsu@example.com"),
            NameCardData("Product Manager", "이영희", "부산광역시 해운대구", "27세/여", "회사원", "younghee@example.com")
            // 추가 카드 데이터...
        )*/



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
