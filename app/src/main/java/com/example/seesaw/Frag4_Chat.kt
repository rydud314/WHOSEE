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
import com.google.android.material.navigation.NavigationView

class Frag4_Chat : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    companion object{
        fun newInstance() : Frag4_Chat{
            return Frag4_Chat()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_frag4_chat, container, false)

        // drawerLayout과 navView 초기화
        drawerLayout = view.findViewById(R.id.drawer_layout_frag4_chat)
        navView = view.findViewById(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawerLayout = view.findViewById(R.id.drawer_layout_frag4_chat)
        navView = view.findViewById(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)

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
            // 추가할 항목을 여기에 넣을 수 있습니다.
        }
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }


}
