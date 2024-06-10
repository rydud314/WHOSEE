package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Frag1_Home : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

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

        drawerLayout = view.findViewById(R.id.drawer_layout_frag1_home)
        navView = view.findViewById(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)

//        val toolbar: androidx.appcompat.widget.Toolbar = view.findViewById(R.id.toolbar)
//        (activity as AppCompatActivity).setSupportActionBar(toolbar)
//        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        // 상태 설명 문자열 없이 ActionBarDrawerToggle 생성자 사용
//        val toggle = ActionBarDrawerToggle(
//            requireActivity(), drawerLayout, toolbar,
//            0, 0
//        )
//
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()

        setHasOptionsMenu(true)

        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val uid = currentUser?.uid.toString()

        var cardId = ""

        if (uid != null) {
            firestore.collection("my_card_list").document(uid).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document != null && document.exists()) {
                        val cards = document.get("cards") as? List<Map<String, Any>>
                        if (cards != null) {
                            for (card in cards) {
                                cardId = card["cardId"].toString()
                            }
                        }
                    }
                }
            }
        }

        val cards = listOf(
            NameCardData("오지윤", "UXUI Designer", "안녕하슈", "영남대학교", "28세", "여", "뉴비", "010-1234-5678", "nayeon@naver.com", "nayeon",""),
            NameCardData("김철수", "UXUI Designer", "안녕하소", "영남대학교", "28세", "남", "뉴비", "010-1234-5678", "hyeonsoo@naver.com", "hyeonsoo","")
        )

        viewPager.adapter = NameCardAdapter(cards)

        TabLayoutMediator(tabLayout, viewPager) { tab, position -> }.attach()

        val btn_make_card: Button = view.findViewById(R.id.btn_make_card)
        btn_make_card.setOnClickListener {
            val intent = Intent(context, MakeCard::class.java)
            startActivity(intent)
        }

        val btn_edit_card: Button = view.findViewById(R.id.btn_edit_card)
        btn_edit_card.setOnClickListener {
            val intent = Intent(context, CardList_to_Edit::class.java)
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

        val menuButton: Button = view.findViewById(R.id.menu_button)
        menuButton.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.END)
        }
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
