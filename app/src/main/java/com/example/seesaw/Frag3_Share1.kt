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

class Frag3_Share1 : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var adapter: Frag3_Share1Adapter

    private var myCardList: ArrayList<Card> = arrayListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_frag3_share1, container, false)

        // drawerLayout과 navView 초기화
        drawerLayout = view.findViewById(R.id.drawer_layout_frag3_share1)
        navView = view.findViewById(R.id.navigation_view)
        navView.setNavigationItemSelectedListener(this)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // 인텐트로부터 데이터 수신
        arguments?.let {
            myCardList = it.getSerializable("myCardList") as ArrayList<Card>
            loadCards(recyclerView)
        }

        return view
    }

    private fun loadCards(recyclerView: RecyclerView) {
        adapter = Frag3_Share1Adapter(myCardList, requireContext())
        recyclerView.adapter = adapter
    }

    companion object {
        fun newInstance(myCardList: ArrayList<Card>): Frag3_Share1 {
            val fragment = Frag3_Share1()
            val args = Bundle()
            args.putSerializable("myCardList", myCardList)
            fragment.arguments = args
            return fragment
        }
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
        drawerLayout.closeDrawer(GravityCompat.END)
        return true
    }
}
