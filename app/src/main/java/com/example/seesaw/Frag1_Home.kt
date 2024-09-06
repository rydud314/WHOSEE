package com.example.seesaw

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Frag1_Home : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var viewModel:CardViewModel

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private var myCardList : ArrayList<Card> = arrayListOf()


    companion object {
        fun newInstance(): Frag1_Home {
            return Frag1_Home()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_frag1_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ViewModel 인스턴스 생성
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CardViewModel::class.java)

        drawerLayout = view.findViewById(R.id.drawer_layout_frag1_home)
        navView = view.findViewById(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)

        setHasOptionsMenu(true)

        // ViewPager2 설정
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val uid = currentUser?.uid.toString()
        Log.d(ContentValues.TAG, "UID =  $uid")

        var cardId = ""
        var card_list = mutableListOf<String>()

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
                                    Log.d(ContentValues.TAG, "result.document is not null")

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

                                        Log.d(ContentValues.TAG, "cardData => $name($cardId)")
                                        myCardList.add(Card(name, position, workplace, email, cardId, gender, imageName, introduction, job, pofol, sns, tel))
                                    }
                                }
                                Log.d(TAG, "${myCardList.size}")

                                myCardList.sortBy { it.name }

                                // ViewPager2에 어댑터 설정
                                viewPager.adapter = NameCardAdapter(myCardList)

                                // TabLayout과 ViewPager2를 연결
                                TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                                    // 탭을 초기화하지 않음
                                }.attach()
                                viewModel.myCardList = myCardList
                                Log.d("frag Home : ", "${viewModel.myCardList.size}")

                                //메인에 명함리스트 정보 넘기기
                                val mainActivity = activity as MainActivity
                                mainActivity.getMyCardList(myCardList)


                            }.addOnFailureListener{ Log.d(TAG, "querysnapshot 실패") }

                        } else {
                            Log.d(TAG, "보유한 명함이 없습니다.") }
                    } else {
                        Log.d(TAG, "No such document")
                        // ViewPager2에 어댑터 설정
                        viewPager.adapter = NameCardAdapterEmpty()

                        // TabLayout과 ViewPager2를 연결
                        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                            // 탭을 초기화하지 않음
                        }.attach()
                    }
                } else { Log.d(TAG, "get failed with ", task.exception) }
            }
        } else{ Log.d(TAG, "when check the my card, uid is null") }

        Log.d(TAG, "second : ${myCardList.size}")

        /*
        // 카드 데이터 목록
        val cards = listOf(
            NameCardData("UXUI Designer", "오지윤", "서울 특별시 성북구", "28세/여", "프리랜서", "knk0208@naver.com"),
            NameCardData("UXUI Designer", "김철수", "서울 특별시 강남구", "30세/남", "회사원", "chulsu@example.com"),
            NameCardData("Product Manager", "이영희", "부산광역시 해운대구", "27세/여", "회사원", "younghee@example.com")
            // 추가 카드 데이터...
        )*/



        val btn_make_card: Button = view.findViewById(R.id.btn_make_card)
        btn_make_card.setOnClickListener {
            val intent = Intent(context, MakeCard::class.java)
            startActivity(intent)

        }

        val btn_edit_card: Button = view.findViewById(R.id.btn_chat)
        btn_edit_card.setOnClickListener {
            val intent = Intent(context, ChooseEditCard::class.java)
            intent.putExtra("myCardList", myCardList)
            startActivity(intent)
        }

        val btn_camera: Button = view.findViewById(R.id.btn_camera_scan)
        btn_camera.setOnClickListener {
            val intent = Intent(context, CameraActivity::class.java)
            startActivity(intent)
        }

        val btn_report: Button = view.findViewById(R.id.btn_open_report)
        btn_report.setOnClickListener {
            val intent = Intent(context, ChooseReport::class.java)
            startActivity(intent)
        }

        val btn_calender : Button = view.findViewById(R.id.btn_gotocalendar)
        btn_calender.setOnClickListener{
            val intent = Intent(context, Calender::class.java)
            startActivity(intent)
        }

//        val menuButton: Button = view.findViewById(R.id.menu_button)
//        menuButton.setOnClickListener {
//            drawerLayout.openDrawer(GravityCompat.END)
//        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_account -> {
                val intent = Intent(context, AccountActivity::class.java)
                startActivity(intent)
            }
        }
        //drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }
}
