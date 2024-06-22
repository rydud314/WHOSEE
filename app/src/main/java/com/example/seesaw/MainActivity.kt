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
////이거 아니야?
//package com.example.seesaw
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.res.ColorStateList
//import android.os.Bundle
//import android.view.Menu
//import android.view.MenuItem
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.widget.Toolbar
//import androidx.core.content.ContextCompat
//import com.example.seesaw.databinding.ActivityMainBinding
//import com.google.android.material.bottomnavigation.BottomNavigationView
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//
//class MainActivity : AppCompatActivity() {
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
//    // 초기 FAB 색상 정의
//    private var initialFabIconColor: ColorStateList? = null
//    private var initialFabBackgroundColor: ColorStateList? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        // View Binding 초기화
//
//
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        setContentView(R.layout.toolbar_home)
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//        setContentView(R.layout.activity_main)
//        val fragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.main_frame, Frag1_Home.newInstance())
//        fragmentTransaction.commit()
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
//
//
//        // FAB 초기 색상 저장
//        initialFabIconColor = binding.fab.imageTintList
//        initialFabBackgroundColor = binding.fab.backgroundTintList
//
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
//
//        // FAB 클릭 리스너 설정
//        binding.fab.setOnClickListener {
////            // 클릭 시 아이콘 색상 변경
////            it as FloatingActionButton
////            it.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_item_color_selector))
////
////            // 클릭 시 배경 색상 변경
////            it.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.nav_item_color_selector))
//
//            // 클릭 시 아이콘 및 배경 색상 변경
//            binding.fab.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.selected_color))
//            binding.fab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.selected_color))
//
//            // FAB 클릭 시 Frag3_Share 프래그먼트로 화면 전환
//            switchToShareFragment()
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.toolbar_menu, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            android.R.id.home -> {
//                true
//            }
//            R.id.action_account -> {
//                startActivity(Intent(this, AccountActivity::class.java))
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//    private fun switchToShareFragment() {
//        frag3Share = Frag3_Share.newInstance()
//        supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag3Share).commit()
//        binding.bottomNav.menu.findItem(R.id.bottom_nav_share).isChecked = true
//    }
//
//    private val onBottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
//        // 다른 아이템 선택 시 FAB 색상을 초기 상태로 복원
//        binding.fab.imageTintList = initialFabIconColor
//        binding.fab.backgroundTintList = initialFabBackgroundColor
//
//        when (item.itemId) {
//            R.id.bottom_nav_home -> {
//                frag1Home = Frag1_Home.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag1Home).commit()
//            }
//            R.id.bottom_nav_wallet -> {
//                frag2Wallet = Frag2_Wallet.newInstance()
//                supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag2Wallet).commit()
//            }
//            R.id.bottom_nav_share -> {
//                switchToShareFragment()
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
//}
package com.example.seesaw

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.seesaw.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel:CardViewModel

    private lateinit var frag1Home: Frag1_Home
    private lateinit var frag2Wallet: Frag2_Wallet
    private lateinit var frag3Share: Frag3_Share
    private lateinit var frag4Chat: Frag4_Chat
    private lateinit var frag5Auction: Frag5_Auction

    // View Binding 인스턴스 선언
    private lateinit var binding: ActivityMainBinding

    // 초기 qr코드 생성버튼 색상 정의
    private var initialFabIconColor: ColorStateList? = null
    private var initialFabBackgroundColor: ColorStateList? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // View Binding 초기화
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //ViewModel 인스턴스 생성
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(CardViewModel::class.java)
        Log.d("mainAct : ", "초기화 -> ${viewModel.myCardList.size}")


        // 툴바 설정
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        // qr코드 생성 버튼 초기 색상 저장
        initialFabIconColor = binding.makeQrBtn.imageTintList
        initialFabBackgroundColor = binding.makeQrBtn.backgroundTintList

        // bottomNavigationView를 직접 참조하는 대신에 binding을 통해 접근
        binding.bottomNav.setOnNavigationItemSelectedListener(onBottomNavItemSelectedListener)

        // 프래그먼트 초기화 및 첫 화면 설정
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, Frag1_Home.newInstance())
                .commit()
        }



        // qr코드 생성버튼 리스너 설정
        binding.makeQrBtn.setOnClickListener {
            // 클릭 시 아이콘 및 배경 색상 변경
            binding.makeQrBtn.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.selected_color))
            binding.makeQrBtn.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.selected_color))

            // qr코드 생성버튼 클릭 시 Frag3_Share 프래그먼트로 화면 전환
            switchToShareFragment()
        }

        val cardId = intent?.getSerializableExtra("uriExist").toString()
        if(cardId != null){
            Log.d(TAG, "main cardId = $cardId")
            val intent = Intent(this, ShareCardDetail::class.java)
            intent.putExtra("uriExist", cardId)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                true
            }
            R.id.action_account -> {
                startActivity(Intent(this, AccountActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun switchToShareFragment() {
        frag3Share = Frag3_Share.newInstance()
        supportFragmentManager.beginTransaction().replace(R.id.main_frame, frag3Share).commit()
        binding.bottomNav.menu.findItem(R.id.bottom_nav_share).isChecked = true
    }

    private val onBottomNavItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        // 다른 아이템 선택 시 FAB 색상을 초기 상태로 복원
        binding.makeQrBtn.imageTintList = initialFabIconColor
        binding.makeQrBtn.backgroundTintList = initialFabBackgroundColor

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



