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
import com.google.android.material.navigation.NavigationView

class Frag5_Auction : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    companion object{
        fun newInstance() : Frag5_Auction{
            return Frag5_Auction()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_frag5_auction, container, false)

        // drawerLayout과 navView 초기화
        drawerLayout = view.findViewById(R.id.drawer_layout_frag5_auction)
        navView = view.findViewById(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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