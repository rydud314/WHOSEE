package com.example.seesaw

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Frag2_Wallet : Fragment() {

    private var view: View? = null

    companion object{
        fun newInstance() : Frag2_Wallet{
            return Frag2_Wallet()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //view = inflater.inflate(R.layout.activity_frag2_wallet, container, false)
        return inflater.inflate(R.layout.activity_frag2_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val profileList = arrayListOf(
            Profiles(R.drawable.profile_img_1, "오지윤", "급한 용무는 전화 부탁드립니다."),
            Profiles(R.drawable.profile_img_2, "박민혁", "6/7~14일 휴가입니다."),
            Profiles(R.drawable.profile_img_3, "김미리", "오후 반차냈습니다.")
        )

        // RecyclerView를 찾습니다
        val recyclerView: RecyclerView = view.findViewById(R.id.rv_profile)

        recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.setHasFixedSize(true)

        recyclerView.adapter = ProfileAdapter(profileList)
    }
}