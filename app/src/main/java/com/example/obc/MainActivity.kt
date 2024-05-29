package com.example.obc

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.obc.fragment.ChatFragment
import com.example.obc.fragment.HomeFragment
import com.example.obc.fragment.ProfileFragment
import com.example.obc.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, HomeFragment())
            commit()
        }
        binding.bottomNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeMenu -> {
                    // 달력 메뉴 아이템 선택 시 수행할 작업
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainer, HomeFragment())
                        commit()
                    }
                    true
                }
                R.id.chatMenu -> {
                    // 직원 관리 메뉴 아이템 선택 시 수행할 작업
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainer, ChatFragment())
                        commit()
                    }
                    true
                }
                R.id.cardMenu -> {
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.fragmentContainer, ProfileFragment())
                        commit()
                    }

                    // 사진 메뉴 아이템 선택 시 수행할 작업
                    true
                }
                else -> false
            }
        }

    }
}