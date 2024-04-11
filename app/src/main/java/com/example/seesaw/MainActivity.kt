//package com.example.seesaw
//
//import android.os.Bundle
//import android.view.MenuItem
//import androidx.appcompat.app.AppCompatActivity
//import com.example.seesaw.databinding.ActivityMainBinding
//import com.google.android.material.bottomnavigation.BottomNavigationView
//
//class MainActivity : AppCompatActivity(){
//
//    private lateinit var frag1Home: Frag1_Home
//    private lateinit var frag2Wallet: Frag2_Wallet
//    private lateinit var frag3Share: Frag3_Share
//    private lateinit var frag4Chat: Frag4_Chat
//    private lateinit var frag5Auction: Frag5_Auction
//
//    // View Binding 인스턴스 선언
//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // View Binding 초기화
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // bottomNavigationView를 직접 참조하는 대신에 binding을 통해 접근
//        binding.bottomNav.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)
//
//        // 프래그먼트 초기화 및 첫 화면 설정
//        if (savedInstanceState == null) {
//            supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frame, Frag1_Home.newInstance())
//                .commit()
//        }
//    }
//
//    private val onBottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
//
//        when(it.itemId){
//            R.id.bottom_nav_home -> {
//                frag1Home = Frag1_Home.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag1Home).commit()
//            }
//            R.id.bottom_nav_wallet -> {
//                frag2Wallet = Frag2_Wallet.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag2Wallet).commit()
//            }
//            R.id.bottom_nav_share -> {
//                frag3Share = Frag3_Share.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag3Share).commit()
//            }
//            R.id.bottom_nav_chat -> {
//                frag4Chat = Frag4_Chat.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag4Chat).commit()
//            }
//            R.id.bottom_nav_auction -> {
//                frag5Auction = Frag5_Auction.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag5Auction).commit()
//            }
//        }
//        true
//    }
//
//}

package com.example.seesaw

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.seesaw.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var frag1Home: Frag1_Home
    private lateinit var frag2Wallet: Frag2_Wallet
    private lateinit var frag3Share: Frag3_Share
    private lateinit var frag4Chat: Frag4_Chat
    private lateinit var frag5Auction: Frag5_Auction

    // View Binding 인스턴스 선언
    private lateinit var binding: ActivityMainBinding

    // 초기 FAB 색상 정의
    private var initialFabIconColor: ColorStateList? = null
    private var initialFabBackgroundColor: ColorStateList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // FAB 초기 색상 저장
        initialFabIconColor = binding.fab.imageTintList
        initialFabBackgroundColor = binding.fab.backgroundTintList


        // bottomNavigationView를 직접 참조하는 대신에 binding을 통해 접근
        binding.bottomNav.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)

        // 프래그먼트 초기화 및 첫 화면 설정
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, Frag1_Home.newInstance())
                .commit()
        }

        // FAB 클릭 리스너 설정
        binding.fab.setOnClickListener {
//            // 클릭 시 아이콘 색상 변경
//            it as FloatingActionButton
//            it.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_item_color_selector))
//
//            // 클릭 시 배경 색상 변경
//            it.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_item_color_selector))

            // 클릭 시 아이콘 및 배경 색상 변경
            binding.fab.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.selected_color))
            binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.selected_color))

            // FAB 클릭 시 Frag3_Share 프래그먼트로 화면 전환
            switchToShareFragment()
        }
    }

    private fun switchToShareFragment() {
        frag3Share = Frag3_Share.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag3Share).commit()
        binding.bottomNav.menu.findItem(R.id.bottom_nav_share).isChecked = true
    }

    private val onBottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        // 다른 아이템 선택 시 FAB 색상을 초기 상태로 복원
        binding.fab.imageTintList = initialFabIconColor
        binding.fab.backgroundTintList = initialFabBackgroundColor

        when (item.itemId) {
            R.id.bottom_nav_home -> {
                frag1Home = Frag1_Home.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag1Home).commit()
            }
            R.id.bottom_nav_wallet -> {
                frag2Wallet = Frag2_Wallet.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag2Wallet).commit()
            }
            R.id.bottom_nav_share -> {
                switchToShareFragment()
            }
            R.id.bottom_nav_chat -> {
                frag4Chat = Frag4_Chat.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag4Chat).commit()
            }
            R.id.bottom_nav_auction -> {
                frag5Auction = Frag5_Auction.newInstance()
                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag5Auction).commit()
            }
        }
        true
    }
}

