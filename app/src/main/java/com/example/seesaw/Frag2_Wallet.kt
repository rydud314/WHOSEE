package com.example.seesaw

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView

class Frag2_Wallet : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_frag2_wallet, container, false)

        // drawerLayout과 navView 초기화
        drawerLayout = view.findViewById(R.id.drawer_layout_frag2_wallet)
        navView = view.findViewById(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 샘플 데이터
        val cardList = listOf(
            Card("구교영", "Software Engineer", "영남대학교"),
            Card("홍아랑", "Software Engineer", "영남대학교"),
            Card("박해세", "Software Engineer", "영남대학교"),
            Card("Lucy", "Software Engineer", "영남대학교"),
            Card("홍알랑", "Software Engineer", "영남대학교"),
            Card("꼬랑지", "Software Engineer", "영남대학교"),
            Card("김나연", "Software Engineer", "영남대학교"),
            Card("구영교", "Software Engineer", "영남대학교"),
            Card("김현수", "Graphic Designer", "영남대학교")
            // 카드 추가 여기서 하기
        )

        recyclerView.adapter = CardAdapter(cardList, requireContext())
        return view
    }
    companion object{
        fun newInstance() : Frag2_Wallet{
            return Frag2_Wallet()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }
}